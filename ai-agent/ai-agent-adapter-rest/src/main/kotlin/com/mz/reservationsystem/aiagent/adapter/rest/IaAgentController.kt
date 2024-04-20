package com.mz.reservationsystem.aiagent.adapter.rest

import dev.langchain4j.model.chat.ChatLanguageModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


data class Message(val text: String)

@RestController
@RequestMapping("/ai-agent")
class IaAgentController(
    private val localMllModel: ChatLanguageModel
) {

    @GetMapping("/hello")
    suspend fun hello(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello from AI Agent!")
    }

    @PostMapping("/chats")
    suspend fun chat(@RequestBody message: Message): ResponseEntity<String> {
        return ResponseEntity.ok(localMllModel.generate(message.text))
    }
}
