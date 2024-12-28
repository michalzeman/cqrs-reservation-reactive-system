package com.mz.reservationsystem.domain.timeslot

import com.mz.ddd.common.view.BetweenInstantQuery
import com.mz.ddd.common.view.QueryBoolean
import com.mz.reservationsystem.domain.api.timeslot.TIME_SLOT_DOMAIN_TAG
import kotlinx.datetime.Instant

sealed interface TimeSlotQuery

data class FindTimeSlotsByConditions(val conditions: Set<TimeSlotQuery>) : TimeSlotQuery

data class FindTimeSlotBetweenTimes(
    val startTime: Instant,
    val endTime: Instant
) : TimeSlotQuery

internal fun FindTimeSlotBetweenTimes.toBetweenInstantQuery() = BetweenInstantQuery(
    startTime = startTime,
    endTime = endTime,
    domainTag = TIME_SLOT_DOMAIN_TAG.value,
    startTimePropertyName = TimeSlotProperties.startTime,
    endTimePropertyName = TimeSlotProperties.endTime
)

data class FindTimeSlotByBooked(val booked: Boolean) : TimeSlotQuery

internal fun FindTimeSlotByBooked.toQueryData(): QueryBoolean = QueryBoolean(
    domainTag = TIME_SLOT_DOMAIN_TAG.value,
    propertyName = TimeSlotProperties.booked,
    value = booked
)

class TimeSlotQueryException(message: String) : RuntimeException(message)