package com.mz.common.components.internal

import com.mz.common.components.ApplicationChannelStream
import com.mz.common.components.ChannelMessage
import com.mz.ddd.common.api.domain.Message
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.DisposableBean
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST
import reactor.core.scheduler.Schedulers
import reactor.util.concurrent.Queues

internal class ApplicationChannelStreamImpl : ApplicationChannelStream, DisposableBean {

    companion object {
        private val logger = LogFactory.getLog(ApplicationChannelStream::class.java)
    }

    private val subscriptions = mutableListOf<Disposable>()

    private val messagesSink = Sinks.many().multicast().onBackpressureBuffer<ChannelMessage>(Queues.SMALL_BUFFER_SIZE)

    override fun destroy() {
        subscriptions.forEach { Result.runCatching { it.dispose() } }
        subscriptions.clear()
    }

    override fun publish(message: ChannelMessage) {
        messagesSink.emitNext(message, FAIL_FAST)
    }

    override fun messagesStream(): Flux<Message> {
        return messagesSink.asFlux()
            .publishOn(Schedulers.boundedElastic())
            .map { it.payload }
    }

    override fun <M : Message, R> subscribeToChannel(
        messageClass: Class<M>,
        handler: (M) -> Mono<R>,
        filter: ((Message) -> Boolean)?
    ): Disposable {

        val stream = messagesSink.asFlux()
            .publishOn(Schedulers.boundedElastic())
            .filter { messageClass.isInstance(it.payload) }
            .filter { filter?.invoke(messageClass.cast(it.payload)) ?: true }
            .flatMap { message ->
                handler(messageClass.cast(message.payload))
                    .doOnNext { message.success() }
                    .doOnError { message.failure(it) }
            }
            .doOnError { logger.error(it.message, it) }
            .onErrorComplete()
            .repeat()

        val disposable = stream.subscribe()
        subscriptions.add(disposable)

        return disposable
    }
}