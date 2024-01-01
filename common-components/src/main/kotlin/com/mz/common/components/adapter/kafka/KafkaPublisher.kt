package com.mz.common.components.adapter.kafka

import com.mz.ddd.common.api.domain.Message
import kotlinx.coroutines.reactor.mono
import org.apache.commons.logging.LogFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.kafka.sender.KafkaSender
import reactor.util.concurrent.Queues

/**
 * KafkaPublisher is a class that is responsible for publishing messages to a Kafka topic.
 *
 * @param M The type of the message.
 * @property kafkaSender The KafkaSender instance used to send messages to Kafka.
 * @property kafkaTopic The Kafka topic to which messages are published.
 * @property keyMapper A function that maps a message to a key for the Kafka record.
 * @property valueMapper A function that maps a message to a value for the Kafka record.
 */
class KafkaPublisher<M : Message>(
    kafkaSender: KafkaSender<String, String>,
    private val kafkaTopic: String,
    private val keyMapper: (M) -> String,
    private val valueMapper: (M) -> String
) {

    companion object {
        private val logger = LogFactory.getLog(KafkaPublisher::class.java)
    }

    private val kafkaSenderOutbound = kafkaSender.createOutbound()

    private val messagesSink = Sinks
        .many()
        .multicast()
        .onBackpressureBuffer<ProducerRecord<String, String>>(Queues.SMALL_BUFFER_SIZE)


    init {
        kafkaSenderOutbound.send(messagesSink.asFlux())
            .then()
            .doOnError { logger.error(it.message, it) }
            .onErrorComplete()
            .repeat()
            .subscribe()
    }

    /**
     * Publish a message to the Kafka topic.
     *
     * @param message The message to be published.
     * @return A Mono<Void> that completes when the message has been published.
     */
    fun publish(message: M): Mono<Void> {
        return mono {
            val value = valueMapper(message)
            val key = keyMapper(message)

            ProducerRecord(kafkaTopic, key, value).apply {
                headers().add(
                    RecordHeader(
                        "correlation_id", message
                            .correlationId.value.encodeToByteArray()
                    )
                )
            }
        }
            .map { messagesSink.emitNext(it, Sinks.EmitFailureHandler.FAIL_FAST) }
            .then()
    }
}