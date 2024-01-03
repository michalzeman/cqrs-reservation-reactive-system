package com.mz.customer.adapter.kafka.reservation

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.adapter.kafka.KafkaListener
import com.mz.reservationsystem.domain.api.reservation.ReservationDocument
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ReservationAdapterKafkaConfiguration(
    @Value("\${adapter.kafka.bootstrap.servers}") private val bootstrapServers: String,
    @Value("\${adapter.kafka.consumer.group-id}") private val consumerGroup: String,
    @Value("\${adapter.kafka.topics.reservation-system-event-reservation-document}") private val kafkaTopic: String,
    private val applicationChannelStream: ApplicationChannelStream
) {

    @Bean
    fun reservationKafkaListener(): KafkaListener<ReservationDocument> = KafkaListener(
        bootstrapServers,
        consumerGroup,
        kafkaTopic,
        applicationChannelStream
    )

}