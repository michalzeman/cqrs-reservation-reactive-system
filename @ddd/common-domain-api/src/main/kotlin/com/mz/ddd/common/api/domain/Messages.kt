package com.mz.ddd.common.api.domain

import kotlinx.datetime.Instant

sealed interface Message {
    val correlationId: String
    val createdAt: Instant
}

interface DomainEvent : Message {
    val eventId: String
}

interface DomainCommand : Message {
    val commandId: String
}

interface Document : Message {
    val docId: String
}