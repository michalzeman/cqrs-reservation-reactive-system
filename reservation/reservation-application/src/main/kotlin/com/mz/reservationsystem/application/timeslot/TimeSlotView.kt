package com.mz.reservationsystem.application.timeslot

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.subscribeToChannel
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.view.*
import com.mz.ddd.common.view.OperationType.AND
import com.mz.reservationsystem.domain.api.timeslot.TIME_SLOT_DOMAIN_TAG
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import com.mz.reservationsystem.application.timeslot.TimeSlotProperties.booked
import com.mz.reservationsystem.application.timeslot.TimeSlotProperties.endTime
import com.mz.reservationsystem.application.timeslot.TimeSlotProperties.startTime
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

object TimeSlotProperties {
    val startTime = "startTime"
    val endTime = "endTime"
    val booked = "booked"
}

@Component
class TimeSlotView(
    private val domainViewRepository: DomainViewRepository,
    private val domainViewReadOnlyRepository: DomainViewReadOnlyRepository,
    channelStream: ApplicationChannelStream
) {

    init {
        channelStream.subscribeToChannel<TimeSlotDocument>(::process)
    }

    internal fun process(document: TimeSlotDocument): Mono<Void> {
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
            is FindTimeSlotBetweenTimes -> domainViewReadOnlyRepository
                .find(DomainViewQuery(setOf(query.toBetweenInstantQuery())))

            is FindTimeSlotsByConditions -> findByConditions(query)
            is FindTimeSlotByBooked -> domainViewReadOnlyRepository.find(DomainViewQuery(setOf(query.toQueryData())))
        }.map { it.aggregateId }
    }

    private fun findByConditions(query: FindTimeSlotsByConditions): Flux<DomainView> {
        return query.conditions
            .map {
                when (it) {
                    is FindTimeSlotBetweenTimes -> it.toBetweenInstantQuery()
                    is FindTimeSlotByBooked -> it.toQueryData()
                    is FindTimeSlotsByConditions -> throw TimeSlotQueryException("Recursive conditions are not supported")
                }
            }
            .toSet()
            .let { DomainViewQuery(it, AND) }
            .let { domainViewReadOnlyRepository.find(it) }
    }

}