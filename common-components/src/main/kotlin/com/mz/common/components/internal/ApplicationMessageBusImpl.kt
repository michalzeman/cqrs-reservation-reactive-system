package com.mz.common.components.internal

import com.mz.common.components.ApplicationMessageBus
import com.mz.ddd.common.api.domain.Message
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST
import reactor.core.scheduler.Schedulers

@Service
internal class ApplicationMessageBusImpl : ApplicationMessageBus {

    private val messagesSink = Sinks.many().replay().all<Message>(1)

    override fun publish(message: Message) {
        messagesSink.emitNext(message, FAIL_FAST)
    }

    override fun messages(): Flux<Message> {
        return messagesSink.asFlux().replay().publishOn(Schedulers.parallel())
    }
}