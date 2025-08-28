package com.mz.reservationsystem.application.timeslot

import com.mz.common.components.ApplicationChannelStream
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.view.*
import com.mz.ddd.common.view.OperationType.AND
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class TimeSlotViewTest {

    @Mock
    lateinit var domainViewRepository: DomainViewRepository

    @Mock
    lateinit var domainViewReadOnlyRepository: DomainViewReadOnlyRepository

    @Mock
    lateinit var channelStream: ApplicationChannelStream

    lateinit var cut: TimeSlotView

    @BeforeEach
    fun setUp() {
        //        whenever(channelStream.subscribeToChannel<TimeSlotDocument>(any()))
        cut = TimeSlotView(domainViewRepository, domainViewReadOnlyRepository, channelStream)
    }

    @Test
    fun `find, when condition is between times and is not booked, then result is returned`() {
        val findTimeSlotBetweenTimes = FindTimeSlotBetweenTimes(
            startTime = Instant.parse("2022-01-01T00:00:00Z"),
            endTime = Instant.parse("2022-01-01T23:59:59Z")
        )
        val timeSlotByBooked = FindTimeSlotByBooked(false)
        val query = FindTimeSlotsByConditions(
            setOf(
                findTimeSlotBetweenTimes,
                timeSlotByBooked
            )
        )

        val domainViewQuery =
            DomainViewQuery(setOf(findTimeSlotBetweenTimes.toBetweenInstantQuery(), timeSlotByBooked.toQueryData()), AND)

        whenever(domainViewReadOnlyRepository.find(domainViewQuery)) doReturn Flux.just(
            DomainView(Id("1"), setOf())
        )

        StepVerifier.create(cut.find(query))
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun `find, when condition is between times, then result is returned`() {
        val findTimeSlotBetweenTimes = FindTimeSlotBetweenTimes(
            startTime = Instant.parse("2022-01-01T00:00:00Z"),
            endTime = Instant.parse("2022-01-01T23:59:59Z")
        )
        val query = FindTimeSlotsByConditions(
            setOf(
                findTimeSlotBetweenTimes
            )
        )

        val domainViewQuery =
            DomainViewQuery(setOf(findTimeSlotBetweenTimes.toBetweenInstantQuery()), AND)

        whenever(domainViewReadOnlyRepository.find(domainViewQuery)) doReturn Flux.just(
            DomainView(Id("1"), setOf())
        )

        StepVerifier.create(cut.find(query))
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun `find, when FindTimeSlotByBooked, result is returned`() {
        val findTimeSlotByBooked = FindTimeSlotByBooked(true)
        val query = FindTimeSlotsByConditions(
            setOf(
                findTimeSlotByBooked
            )
        )

        val domainViewQuery =
            DomainViewQuery(setOf(findTimeSlotByBooked.toQueryData()), AND)

        whenever(domainViewReadOnlyRepository.find(domainViewQuery)) doReturn Flux.just(
            DomainView(Id("1"), setOf())
        )

        StepVerifier.create(cut.find(query))
            .expectNextCount(1)
            .verifyComplete()
    }
}
