package com.mz.common.components

import com.mz.ddd.common.api.domain.Message
import reactor.core.publisher.Flux

interface ApplicationMessageBus {
    fun publish(message: Message)
    fun messages(): Flux<Message>
}