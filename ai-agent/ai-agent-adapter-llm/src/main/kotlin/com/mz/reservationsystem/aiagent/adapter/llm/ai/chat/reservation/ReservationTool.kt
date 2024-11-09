package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation

import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.newId
import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import org.apache.commons.logging.LogFactory
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatterBuilder
import kotlin.random.Random

private val logger = LogFactory.getLog(ReservationTool::class.java)

data class TimeSlot(val startTime: Instant, val endTime: Instant, val slotId: Id = newId())

@Component
class ReservationTool {

    internal val formatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm")
        .optionalStart()
        .appendPattern(":ss")
        .optionalEnd()
        .optionalStart()
        .appendPattern("XXX")
        .optionalEnd()
        .toFormatter()

    @Tool("Find available time slots by start and end time, times are in the ISO 8601 formatted `yyyy-MM-dd'T'HH:mmXXX`")
    fun findTimeSlotByTimeWindow(
        @P("Start time, in the ISO 8601") startTime: String,
        @P("End time, in the ISO 8601") endTime: String
    ): List<String> {
        logger.info("findTimeSlotByTimeWindow -> start: $startTime , end: $endTime")
        val randomHours = Random.nextLong(1, 5)
        val startTimeInstant = LocalDateTime.parse(startTime, formatter).toInstant(ZoneOffset.UTC)
        val endTimeInstant = LocalDateTime.parse(endTime, formatter).toInstant(ZoneOffset.UTC)
        val timeSlot1 = TimeSlot(startTimeInstant, startTimeInstant.plusSeconds(240))
        return listOf(timeSlot1.toString())
    }

    @Tool("Create reservation identified by time slot id, times are in the ISO 8601 formatted `yyyy-MM-dd'T'HH:mmXXX`")
    fun createReservation(
        @P("Customer id") customerId: Id,
        @P("Start time, in the ISO 8601") startTime: String,
        @P("End of time, in the ISO 8601") endTime: String,
        @P("customer email provided by customer") email: Email
    ): String {
//        logger.info("createReservation -> $id, email: $email")
        logger.info("createReservation -> customer id: $customerId, start: $startTime, end: $endTime, email: $email")
        return "Reservation has been created with the id: ${newId()}"
    }

}
