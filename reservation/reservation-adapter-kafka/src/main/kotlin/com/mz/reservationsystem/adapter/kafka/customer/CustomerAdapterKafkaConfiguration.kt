package com.mz.reservationsystem.adapter.kafka.customer

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.adapter.kafka.KafkaListener
import com.mz.customer.domain.api.CustomerDocument
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class CustomerAdapterKafkaConfiguration(
    @Value("\${adapter.kafka.bootstrap.servers}") private val bootstrapServers: String,
    @Value("\${adapter.kafka.consumer.group-id}") private val consumerGroup: String,
    @Value("\${adapter.kafka.topics.customer-event-customer-document}") private val kafkaTopic: String,
    private val applicationChannelStream: ApplicationChannelStream
) {

    @Bean
    fun customerKafkaListener(): KafkaListener<CustomerDocument> = KafkaListener(
        bootstrapServers,
        consumerGroup,
        kafkaTopic,
        applicationChannelStream
    )

}