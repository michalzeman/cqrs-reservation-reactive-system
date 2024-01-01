package com.mz.customer.adapter.kafka.wiring

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.adapter.kafka.KafkaPublisher
import com.mz.common.components.subscribeToChannel
import com.mz.customer.domain.api.CustomerDocument
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions


@Component
class CustomerAdapterKafkaConfiguration {

    @Bean
    fun kafkaSender(@Value("\${adapter.kafka.bootstrap.servers}") bootstrapServers: String): KafkaSender<String, String> {
        val producerProps: MutableMap<String, Any> = HashMap()
        producerProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        producerProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        producerProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java

        val senderOptions =
            SenderOptions.create<String, String>(producerProps)
                .maxInFlight(1024)
        return KafkaSender.create(senderOptions)
    }

    @Bean
    fun customerDocumentKafkaProducer(
        @Value("\${adapter.kafka.topics.customer-event-customer-document}") kafkaTopic: String,
        applicationChannelStream: ApplicationChannelStream,
        kafkaSender: KafkaSender<String, String>
    ): KafkaPublisher<CustomerDocument> {
        return KafkaPublisher<CustomerDocument>(
            kafkaSender,
            kafkaTopic,
            keyMapper = { Json.encodeToString(it.aggregateId) },
            valueMapper = { Json.encodeToString(it) }
        ).also { applicationChannelStream.subscribeToChannel<CustomerDocument>(it::publish) }
    }
}
