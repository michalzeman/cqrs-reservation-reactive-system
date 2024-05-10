package com.mz.reservationsystem.aiagent.domain.agent

import dev.langchain4j.service.UserMessage

data class ClassificationResult(val result: Boolean, val reason: String)

interface ChatClassification {

    @UserMessage(
        """
        Is the following text related to the reservation application like operations:
         - create new reservation
         - asking on an existing reservation
         - modification of reservations
      Text: {{it}}
      Response format: 
        - strict lowercase true, false
        - only one word, no other characters
     """
    )
    fun isRelatedToReservationSystem(text: String): Boolean

    @UserMessage(
        """
        Is the following text related to the reservation application like operations:
         - create new reservation
         - asking on an existing reservation
         - modification of reservations
        Text: {{it}}
        Response: 
        - valid strict JSON format with `result` -> boolean, `reason` -> text
        - result -> strict lowercase true, false
        - reason should contain the explanation
        - JSON should be valid and serializable 
    """
    )
    fun relatedToReservationSystem(text: String): ClassificationResult

}