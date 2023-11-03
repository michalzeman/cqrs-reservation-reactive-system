package com.mz.ddd.common.api.domain

import kotlinx.datetime.Instant

sealed interface Message {
    val correlationId: Id
    val createdAt: Instant
}

interface DomainEvent : Message {
    val eventId: Id
}

interface DomainCommand : Message {
    val commandId: Id
}

interface Document<E : DomainEvent> : Message {
    val docId: Id
    val events: Set<E>
}