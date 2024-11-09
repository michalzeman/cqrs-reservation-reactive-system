package com.mz.reservationsystem.aiagent.domain.api.chat

import dev.langchain4j.model.output.structured.Description

enum class ChatAgentType {
    @Description("Registration of new User or Customer")
    USER_REGISTRATION,
    @Description("Creating of the new reservations")
    RESERVATION,
    @Description("Working with an existing reservations of customers or users")
    RESERVATION_VIEW,
    @Description("Not specific topic")
    NONE
}