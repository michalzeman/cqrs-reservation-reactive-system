package com.mz.ddd.common.api.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
sealed class Message {
    abstract val correlationId: String
    abstract val createdAt: Instant
}

@Serializable
abstract class DomainEvent : Message() {
    abstract val eventId: String
}

@Serializable
abstract class DomainCommand : Message() {
    abstract val commandId: String
}

@Serializable
abstract class Document : Message() {
    abstract val docId: String
}