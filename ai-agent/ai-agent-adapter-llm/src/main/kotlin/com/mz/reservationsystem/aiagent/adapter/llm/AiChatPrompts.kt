package com.mz.reservationsystem.aiagent.adapter.llm

import dev.langchain4j.model.input.PromptTemplate

internal val isRelatedToReservationPrompt: PromptTemplate = PromptTemplate.from(
    """
    Analyse the text. Respond only strictly in the `application/json`.
    JSON schema:
    properties:
        result:
            type: boolean
            description: result of the analysis true or false
        reason:
            type: string
            description: reason of the analysis
    
    INPUT: Hi.
    OUTPUT: 
    result: true,
    reason": It is OK, normally started discussion
    
    INPUT: Tell me a story
    OUTPUT:
    result: false,
    reason: "It is not related to the reservation or reservation system"
    
    INPUT: What is the weather today?
    OUTPUT:
    result: false,
    reason: "It is not related to the reservation or reservation system"
       
    INPUT: You are stupid.
    OUTPUT: 
    result: false,
    reason: "Chat is designed to provide relevant information about the reservation, provide support related to existing reservations"
    
    INPUT: I would like to create a reservation.
    OUTPUT:
    result: true,
    reason: "It is OK"
    
    INPUT: Whats are my existing reservations.
    OUTPUT: 
    result: true,
    reason: "It is OK"
    
    INPUT: Whats are my account information.
    OUTPUT: 
    result: true,
    reason: "It is OK"
    
    INPUT: I want to cancel or update the reservation.
    OUTPUT: 
    result: true,
    reason: "It is OK"
    
    INPUT: {{text}}
    OUTPUT:
""".trimIndent()
)

val promptChatTypeJson: PromptTemplate = PromptTemplate.from(
    """
        Analyse chat with chat history if context is changing.
         - USE VALUES: USER_REGISTRATION, RESERVATION_VIEW, RESERVATION, NONE
         - LOGIC OF ANALYSE: Use the chat context or chat history to analyse user intention.
         - Do not hallucinate!
         - You must respond in a valid JSON format.
         - You must not wrap JSON response in backticks, markdown, or in any other way, but return it as plain text.
         - No explanation
         - JSON strict schema:
         {
             "chatUseCase": [[classified value mentioned above]]
         }
         ---
         - INPUT: create new reservation 
         - RESPONSE: { "chatUseCase": "RESERVATION" }         
         - INPUT: Request related to the new Customer or new User or creation of the Account 
         - RESPONSE: { "chatUseCase": "RESERVATION" }
         - INPUT: asking on an existing reservation or providing reservation details
         - RESPONSE: { "chatUseCase": "RESERVATION_VIEW" }
         - INPUT: modification of reservations
         - RESPONSE: { "chatUseCase": "RESERVATION" }
         - INPUT: If is not related to any provided options
         - RESPONSE: { "chatUseCase": "NONE" }
         - INPUT: {{text}}
    """.trimIndent()
    )

    val promptChatType: PromptTemplate = PromptTemplate.from(
        """
        Analyse chat MESSAGE.
         - USE VALUES: USER_REGISTRATION, RESERVATION_VIEW, RESERVATION, NONE
         - You must respond only with valid [[USE VALUES]].
         - You must not wrap response in backticks, markdown, or in any other way, but return it as plain text.
         - No explanation
         ---
         - INPUT: create new reservation 
         - RESPONSE: RESERVATION         
         - INPUT: Request related to the new Customer or new User or creation of the Account 
         - RESPONSE: RESERVATION
         - INPUT: asking on an existing reservation or providing reservation details
         - RESPONSE: RESERVATION_VIEW
         - INPUT: modification of reservations
         - RESPONSE: RESERVATION
         - INPUT: If is not related to any provided options
         - RESPONSE: NONE
         - MESSAGE to analyse: {{text}}
    """.trimIndent()
)

