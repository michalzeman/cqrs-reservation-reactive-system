package agent

import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.RegistrationAgent
import com.mz.reservationsystem.aiagent.adapter.llm.storage.AiChatMemoryStorageConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.wiring.AgentAiServicesConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.wiring.OllamaLlmModelConfiguration
import com.mz.reservationsystem.aiagent.domain.ai.AgentManager
import com.mz.reservationsystem.aiagent.domain.ai.model.ChatResponse
import com.mz.reservationsystem.aiagent.domain.ai.model.NewChatRequest
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled
@SpringBootTest(classes = [TestAiAgentConfiguration::class, OllamaLlmModelConfiguration::class, AgentAiServicesConfiguration::class, AiChatMemoryStorageConfiguration::class])
@ActiveProfiles("ollama", "test")
class CustomerAgentTest {

    @Autowired
    private lateinit var agentManager: AgentManager

    @Autowired
    lateinit var registrationAgent: RegistrationAgent

    @Test
    fun `AgentManager, register new user`() = runBlocking {
        val agentRequest = NewChatRequest(
            Content(
                """
            Register new customer 
            - first name: Michal
            - last name: Zeman
            - email: test@test.com
        """.trimIndent()
            )
        )

        agentManager.execute(agentRequest).collect {
            print("${it.message.value} ")
        }
    }

    @Test
    fun `CustomerAgent, load existing customer and classify customer is true`(): Unit = runBlocking {
        val agentRequest = NewChatRequest(
            Content(
                """
                My user id is 9cafa14d-7a24-4b02-ac98-015b665b4007
                """.trimIndent()
            )
        )

        println("User: \n${agentRequest.message.value}")

        val responseFlow = agentManager.execute(agentRequest)
        val answer = responseFlow
            .map { it as ChatResponse }
            .reduce(aggregateAgentResponseFlow())

        println("Agent: ${answer.message.value}")

        assertThat(registrationAgent.isCustomerIdentified(answer.chatId)).isTrue()
    }

    @Test
    fun `CustomerAgent, when is customer data are not present then classification of customer is false`(): Unit =
        runBlocking {
            val agentRequest = NewChatRequest(
                Content(
                    """
                    Hi, how are you?
                    """.trimIndent()
                )
            )

            println("User: \n${agentRequest.message.value}")

            val responseFlow = agentManager.execute(agentRequest)
            val answer = responseFlow.map { it as ChatResponse }
                .reduce(aggregateAgentResponseFlow())

            println("Agent: ${answer.message.value}")

            assertThat(registrationAgent.isCustomerIdentified(answer.chatId)).isFalse()
        }
}