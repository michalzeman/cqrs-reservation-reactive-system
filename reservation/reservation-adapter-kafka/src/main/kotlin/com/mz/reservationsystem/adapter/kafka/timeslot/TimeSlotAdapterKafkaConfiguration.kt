package com.mz.reservationsystem.adapter.kafka.timeslot

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.adapter.kafka.KafkaPublisher
import com.mz.common.components.subscribeToChannel
import com.mz.reservationsystem.domain.api.timeslot.TimeSlotDocument
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender


@Component
class TimeSlotAdapterKafkaConfiguration {

    @Bean
    fun timeSlotDocumentKafkaPublisher(
        @Value("\${adapter.kafka.topics.reservation-system-event-time-slot-document}") kafkaTopic: String,
        applicationChannelStream: ApplicationChannelStream,
        kafkaSender: KafkaSender<String, String>
    ): KafkaPublisher<TimeSlotDocument> {
        return KafkaPublisher<TimeSlotDocument>(
            kafkaSender,
            kafkaTopic,
            keyMapper = { Json.encodeToString(it.aggregateId) },
            valueMapper = { Json.encodeToString(it) }
        ).also { applicationChannelStream.subscribeToChannel<TimeSlotDocument>(it::publish) }
    }
}
