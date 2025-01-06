package com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation

import com.mz.ddd.common.api.domain.Email
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.newId
import com.mz.reservationsystem.aiagent.domain.customer.CreateReservation
import com.mz.reservationsystem.aiagent.domain.customer.CustomerRepository
import com.mz.reservationsystem.aiagent.domain.reservation.FindTimeSlotByTimeWindow
import com.mz.reservationsystem.aiagent.domain.reservation.ReservationRepository
import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.apache.commons.logging.LogFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatterBuilder

private val logger = LogFactory.getLog(ReservationTool::class.java)

data class TimeSlot(val startTime: Instant, val endTime: Instant, val slotId: Id = newId())

@Component
@Profile("!test-ai")
class ReservationTool(
    private val customerRepository: CustomerRepository,
    private val reservationRepository: ReservationRepository
) {

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
    ): List<String> = runBlocking {
        logger.info("findTimeSlotByTimeWindow -> start: $startTime , end: $endTime")

        val startTimeInstant = LocalDateTime.parse(startTime, formatter).toInstant(ZoneOffset.UTC)
        val endTimeInstant = LocalDateTime.parse(endTime, formatter).toInstant(ZoneOffset.UTC)

        reservationRepository.findTimeSlotByTimeWindow(
            FindTimeSlotByTimeWindow(
                startTime = startTimeInstant,
                endTime = endTimeInstant
            )
        )
            .toList()
            .map { it.toString() }
    }

    @Tool("Create reservation identified by time slot id, times are in the ISO 8601 formatted `yyyy-MM-dd'T'HH:mmXXX`")
    fun createReservation(
        @P("Customer id") customerId: String,
        @P("Start time, in the ISO 8601") startTime: String,
        @P("End of time, in the ISO 8601") endTime: String,
        @P("customer email provided by customer") email: String
    ): String = runBlocking {
        logger.info("createReservation -> customer id: $customerId, start: $startTime, end: $endTime, email: $email")

        val startTimeInstant = LocalDateTime.parse(startTime, formatter).toInstant(ZoneOffset.UTC)
        val endTimeInstant = LocalDateTime.parse(endTime, formatter).toInstant(ZoneOffset.UTC)
        val request = CreateReservation(
            customerId = Id(customerId),
            email = Email(email),
            startTime = startTimeInstant,
            endTime = endTimeInstant
        )

        val reservationId = customerRepository.createReservation(request)
        "Reservation has been created with the id: $reservationId"
    }

}
