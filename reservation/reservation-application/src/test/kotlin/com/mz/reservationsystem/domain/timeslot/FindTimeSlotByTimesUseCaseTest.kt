package com.mz.reservationsystem.application.timeslot

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.Version
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class FindTimeSlotByTimesUseCaseTest {

    @Mock
    private lateinit var timeSlotAggregateManager: TimeSlotAggregateManager

    @Mock
    private lateinit var timeSlotView: TimeSlotView

    @InjectMocks
    private lateinit var cut: FindTimeSlotByTimesUseCase

    @Test
    fun invoke() {
        val query = FindTimeSlotBetweenTimes(
            startTime = Instant.parse("2022-01-01T00:00:00Z"),
            endTime = Instant.parse("2022-01-01T23:59:59Z")
        )

        whenever(timeSlotView.find(query)) doReturn Flux.just(
            Id("1"),
        )

        whenever(timeSlotAggregateManager.findById(Id("1"))) doReturn
            TimeSlotDocument(
                aggregateId = Id("1"),
                startTime = Instant.parse("2022-01-01T00:00:00Z"),
                endTime = Instant.parse("2022-01-01T23:59:59Z"),
                booked = false,
                version = Version(1),
                valid = true
            ).toMono()

        StepVerifier.create(cut(query))
            .expectNextCount(1)
            .verifyComplete()
    }
}