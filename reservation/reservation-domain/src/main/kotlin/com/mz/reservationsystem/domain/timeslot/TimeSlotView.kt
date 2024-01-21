package com.mz.reservationsystem.domain.timeslot

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.subscribeToChannel
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.view.BetweenInstantQuery
import com.mz.ddd.common.view.DomainViewReadOnlyRepository
import com.mz.ddd.common.view.DomainViewRepository
import com.mz.ddd.common.view.QueryableBoolean
import com.mz.ddd.common.view.QueryableInstant
import com.mz.reservationsystem.domain.api.timeslot.TIME_SLOT_DOMAIN_TAG
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import com.mz.reservationsystem.domain.timeslot.TimeSlotProperties.booked
import com.mz.reservationsystem.domain.timeslot.TimeSlotProperties.endTime
import com.mz.reservationsystem.domain.timeslot.TimeSlotProperties.startTime
import kotlinx.datetime.Instant
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

object TimeSlotProperties {
    val startTime = "startTime"
    val endTime = "endTime"
    val booked = "booked"
}

sealed interface TimeSlotQuery

data class FindTimeSlotBetweenTimes(
    val startTime: Instant,
    val endTime: Instant
) : TimeSlotQuery

@Component
class TimeSlotView(
    private val domainViewRepository: DomainViewRepository,
    private val domainViewReadOnlyRepository: DomainViewReadOnlyRepository,
    private val channelStream: ApplicationChannelStream
) {

    init {
        channelStream.subscribeToChannel<TimeSlotDocument>(::process)
    }

    fun process(document: TimeSlotDocument): Mono<Void> {
        val startTime = QueryableInstant(
            value = document.startTime,
            aggregateId = document.aggregateId.value,
            domainTag = TIME_SLOT_DOMAIN_TAG.value,
            propertyName = startTime,
            timestamp = instantNow()
        )
        val endTime = QueryableInstant(
            value = document.endTime,
            aggregateId = document.aggregateId.value,
            domainTag = TIME_SLOT_DOMAIN_TAG.value,
            propertyName = endTime,
            timestamp = instantNow()
        )
        val booked = QueryableBoolean(
            value = document.booked,
            aggregateId = document.aggregateId.value,
            domainTag = TIME_SLOT_DOMAIN_TAG.value,
            propertyName = booked,
            timestamp = instantNow()
        )
        return domainViewRepository.save(setOf(startTime, endTime, booked))
    }

    fun find(query: TimeSlotQuery): Flux<Id> {
        return when (query) {
            is FindTimeSlotBetweenTimes -> {
                val betweenInstantQuery = BetweenInstantQuery(
                    startTime = query.startTime,
                    endTime = query.endTime,
                    domainTag = TIME_SLOT_DOMAIN_TAG.value,
                    startTimePropertyName = startTime,
                    endTimePropertyName = endTime
                )
                domainViewReadOnlyRepository.find(betweenInstantQuery)
            }
        }.map { it.aggregateId }
    }

}