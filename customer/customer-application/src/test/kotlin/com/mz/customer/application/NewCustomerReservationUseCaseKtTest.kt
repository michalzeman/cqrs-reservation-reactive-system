package com.mz.customer.application

import com.mz.common.components.ApplicationChannelStream
import com.mz.customer.domain.api.*
import com.mz.ddd.common.api.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.time.Duration


@ExtendWith(MockitoExtension::class)
class NewCustomerReservationUseCaseKtTest {

    @Mock
    private lateinit var customerApi: CustomerApi

    @Mock
    private lateinit var channelStream: ApplicationChannelStream

    private lateinit var useCase: NewCustomerReservationUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = NewCustomerReservationUseCase(customerApi, channelStream)
    }

    @Test
    fun `invoke should return CustomerDocument on success`() {
        // Given
        val customerId = newId()
        val command = RequestNewCustomerReservation(customerId, newId(), ReservationPeriod(instantNow(), instantNow()))
        val customerDocument = CustomerDocument(
            customerId, LastName("Doe"), FirstName("John"), Email("test@test.com"), Version(),
            events = setOf(
                CustomerReservationConfirmed(newId(), command.requestId, newId()),
            ),
        )
        val customerDocumentWithReservationRequested = CustomerDocument(
            customerId, LastName("Doe"), FirstName("John"), Email("test@test.com"), Version(), events = setOf(
                CustomerReservationRequested(command.requestId, command.reservationPeriod, customerId)
            )
        )

        whenever(customerApi.execute(command)).thenReturn(customerDocumentWithReservationRequested.toMono())
        whenever(channelStream.messagesStream()).thenReturn(Flux.just(customerDocument))

        // When
        val result = useCase.invoke(command)

        // Then
        StepVerifier.create(result).expectNext(customerDocument).verifyComplete()
    }

    @Test
    fun `invoke should timeout if no correlated message is received`() {
        // Given
        val command = RequestNewCustomerReservation(newId(), newId(), ReservationPeriod(instantNow(), instantNow()))
        val customerDocumentWithReservationRequested = CustomerDocument(
            command.customerId, LastName("Doe"), FirstName("John"), Email("test@test.com"), Version(), events = setOf(
                CustomerReservationRequested(command.requestId, command.reservationPeriod, command.customerId)
            )
        )
        whenever(customerApi.execute(command)).thenReturn(customerDocumentWithReservationRequested.toMono())
        whenever(channelStream.messagesStream()).thenReturn(Flux.never())

        // When
        val result = useCase.invoke(command)

        // Then
        StepVerifier.create(result).expectTimeout(Duration.ofSeconds(5)).verify()
    }

    @Test
    fun `invoke should error if customerApi returns an error`() {
        // Given
        val command = RequestNewCustomerReservation(newId(), newId(), ReservationPeriod(instantNow(), instantNow()))
        whenever(customerApi.execute(command)).thenReturn(Mono.error(RuntimeException("API error")))
        whenever(channelStream.messagesStream()).thenReturn(Flux.never())

        // When
        val result = useCase.invoke(command)

        // Then
        StepVerifier.create(result).expectError(RuntimeException::class.java).verify()
    }

    @Test
    fun `isCorrelatedTo, when message is CustomerDocument and cmd is correlated, then true`() {
        // Given
        val requestId = newId()
        val document = CustomerDocument(
            aggregateId = newId(),
            lastName = LastName("Doe"),
            firstName = FirstName("John"),
            email = Email("test@test.prg"),
            version = Version(),
            events = setOf(
                CustomerReservationConfirmed(
                    reservationId = newId(), requestId = requestId, aggregateId = newId()
                )
            )
        )

        val command = RequestNewCustomerReservation(
            customerId = document.aggregateId,
            requestId = requestId,
            reservationPeriod = ReservationPeriod(instantNow(), instantNow())
        )

        // When
        val result = document.isCorrelatedTo(command)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isCorrelatedTo, when CustomerDocument and event is CustomerReservationConfirmed different requestId, then false`() {
        // Given
        val requestId = newId()
        val document = CustomerDocument(
            aggregateId = newId(),
            lastName = LastName("Doe"),
            firstName = FirstName("John"),
            email = Email("test@test.test"),
            version = Version(),
            events = setOf(
                CustomerReservationConfirmed(
                    reservationId = newId(), requestId = newId(), aggregateId = newId()
                )
            )
        )

        val command = RequestNewCustomerReservation(
            customerId = document.aggregateId,
            requestId = requestId,
            reservationPeriod = ReservationPeriod(instantNow(), instantNow())
        )

        // When
        val result = document.isCorrelatedTo(command)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `isCorrelatedTo, when message is not correlated to the cmd, then false`() {
        // Given
        val requestId = newId()
        val document = CustomerDocument(
            aggregateId = newId(),
            lastName = LastName("Doe"),
            firstName = FirstName("John"),
            email = Email("test@test.org"),
            version = Version(),
        )

        val command = RequestNewCustomerReservation(
            customerId = newId(),
            requestId = requestId,
            reservationPeriod = ReservationPeriod(instantNow(), instantNow())
        )

        // When
        val result = document.isCorrelatedTo(command)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `isCorrelatedTo, when is not CustomerDocument, then false`() {
        // Given
        val requestId = newId()
        val document = CustomerReservationConfirmed(
            reservationId = newId(), requestId = requestId, aggregateId = newId()
        )

        val command = RequestNewCustomerReservation(
            customerId = newId(),
            requestId = requestId,
            reservationPeriod = ReservationPeriod(instantNow(), instantNow())
        )

        // When
        val result = document.isCorrelatedTo(command)

        // Then
        assertThat(result).isFalse()
    }
}