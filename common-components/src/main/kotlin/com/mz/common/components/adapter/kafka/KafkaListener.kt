package com.mz.common.components.adapter.kafka

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.ChannelMessage
import com.mz.ddd.common.api.domain.Message
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.commons.logging.LogFactory
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.util.retry.Retry
import java.time.Duration
import java.time.temporal.ChronoUnit

typealias Decoder<M> = (value: String) -> M

class KafkaListener<M : Message>(
    val bootstrapServers: String,
    val consumerGroup: String,
    val kafkaTopic: String,
    val applicationChannelStream: ApplicationChannelStream,
    val valueDecoder: Decoder<M>
) {

    init {
        KafkaReceiver.create(buildReceiverOptions())
            .receive()
            .flatMap {
                val value = valueDecoder(it.value())
                val channelMessage = ChannelMessage(value)
                applicationChannelStream.publish(channelMessage)
                channelMessage.processed()
            }
            .doOnError { logger.error(it.message, it) }
            .retryWhen(Retry.backoff(3, Duration.of(10L, ChronoUnit.SECONDS)))
            .subscribe()
    }

    companion object {
        private val logger = LogFactory.getLog(KafkaListener::class.java)

        inline operator fun <reified M : Message> invoke(
            bootstrapServers: String,
            consumerGroup: String,
            kafkaTopic: String,
            applicationChannelStream: ApplicationChannelStream,
        ): KafkaListener<M> {
            return KafkaListener(
                bootstrapServers,
                consumerGroup,
                kafkaTopic,
                applicationChannelStream
            ) { Json.decodeFromString<M>(it) }
        }
    }

    private fun buildReceiverOptions(): ReceiverOptions<String, String> {
        val consumerProps: MutableMap<String, Any> = HashMap()
        consumerProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        consumerProps[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroup
        consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        consumerProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java

        return ReceiverOptions.create<String, String>(consumerProps)
            .subscription(setOf(kafkaTopic))
    }

}