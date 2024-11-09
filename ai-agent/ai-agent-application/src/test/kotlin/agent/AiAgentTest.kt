package agent

import com.mz.ddd.common.api.domain.newId
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.AssistantAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationStreamingAgent
import com.mz.reservationsystem.aiagent.adapter.llm.storage.AiChatMemoryStorageConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.wiring.AgentAiServicesConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.wiring.OllamaLlmModelConfiguration
import com.mz.reservationsystem.aiagent.domain.ai.AgentManager
import com.mz.reservationsystem.aiagent.domain.ai.ChatAgentTypeClassification
import com.mz.reservationsystem.aiagent.domain.ai.agent.ChatAgent
import com.mz.reservationsystem.aiagent.domain.ai.agent.asFlow
import com.mz.reservationsystem.aiagent.domain.ai.model.ChatRequest
import com.mz.reservationsystem.aiagent.domain.ai.model.ChatResponse
import com.mz.reservationsystem.aiagent.domain.ai.model.NewChatRequest
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAgentType
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled
@SpringBootTest(classes = [TestAiAgentConfiguration::class, OllamaLlmModelConfiguration::class, AgentAiServicesConfiguration::class, AiChatMemoryStorageConfiguration::class])
@ActiveProfiles("ollama", "test")
//@ActiveProfiles("open-ai", "test")
class AiAgentTest {

    @Autowired
    private lateinit var agentManager: AgentManager

    @Autowired
    private lateinit var chatAgentTypeClassification: ChatAgentTypeClassification

    @Autowired
    private lateinit var chatAgent: ChatAgent

    @Autowired
    private lateinit var assistant: AssistantAgent

    @Autowired
    lateinit var reservationStreamingAgent: ReservationStreamingAgent


    @Test
    fun `test for register customer tool chain`() = runBlocking {
        val chatId = newId()
        val message1 = """
            I want to register new customer
            - first name: Michal
        """.trimIndent()

        println("User: $message1")
        val result1 = chatAgent.userRegistrationChat(chatId, Content(message1))

        println("Agent: $result1")

        val message2 = """
            last name is Zeman
        """.trimIndent()

        println("User: $message2")
        val result2 = chatAgent.userRegistrationChat(chatId, Content(message2))

        println("Agent: $result2")

        val message3 = """
            email is test@test.org
        """.trimIndent()

        println("User: $message3")
        val result3 = chatAgent.userRegistrationChat(chatId, Content(message3))

        println("Agent: $result3")
    }

    @Test
    fun `test for register customer, using complex object tool chain`() = runBlocking {
        val message = """
            Register new customer
            - first name: Michal
            - last name: Zeman
            - email: test@test.com
        """.trimIndent()


        val chatId = newId()

        val result = chatAgent.userRegistrationChat(chatId, Content(message)).asFlow()

        result.collect {
            print("$it ")
        }
    }

    @Test
    fun `test for tool chain, using streaming Agent`(): Unit = runBlocking {
        val result = assistant.chatStream(
            newId(), """
            Register new customer 
            - first name: Michal
            - last name: Zeman
            - email: test@test.com
        """.trimIndent()
        )

        val resultAsString: Flow<String> = result.asFlow()
        resultAsString.collect { print(it) }
    }

    @Test
    fun `function call classification`(): Unit = runBlocking {
        val request = ChatRequest(newId(), Content("I want to make customer registration"))

        val response = agentManager.execute(request)

        Assertions.assertThat(response.count()).isEqualTo(1)
    }

    @Test
    fun `chat type classification`(): Unit = runBlocking {
        val message1 = """
            Give me list of reservations
        """.trimIndent()

        val result1 = chatAgentTypeClassification.classify(Content(message1))
        Assertions.assertThat(result1).isEqualTo(ChatAgentType.RESERVATION_VIEW)

        val messageRegistration = """
            I would like to register my self
        """.trimIndent()

        val resultRegistration = chatAgentTypeClassification.classify(Content(messageRegistration))
        Assertions.assertThat(resultRegistration).isEqualTo(ChatAgentType.USER_REGISTRATION)

        val messageUpdateReservation = """
            I would like to update my reservation
        """.trimIndent()

        val resultUpdateReservation = chatAgentTypeClassification.classify(Content(messageUpdateReservation))
        Assertions.assertThat(resultUpdateReservation).isEqualTo(ChatAgentType.RESERVATION)

        val messageNon = """
            I would like to know the weather
        """.trimIndent()

        val resultNon = chatAgentTypeClassification.classify(Content(messageNon))
        Assertions.assertThat(resultNon).isEqualTo(ChatAgentType.NONE)
    }

    @Test
    fun `create a reservation chat`() = runBlocking {
        val message1 = """
            I would like to create an reservation from 2024-10-12 12:00 to 2024-10-12 14:00
        """.trimIndent()

        println("User: $message1")

        val agentAnswer1 = agentManager.execute(NewChatRequest(Content(message1)))
            .map { it as ChatResponse }
            .reduce(aggregateAgentResponseFlow())
        val chatId = agentAnswer1.chatId
        println("Agent: ")
        println(agentAnswer1.message.value)

        val message2 = """
            customer id is 9cafa14d-7a24-4b02-ac98-015b665b4007
        """.trimIndent()
        println("User: $message2")

        val agentAnswer2 = agentManager.execute(ChatRequest(chatId, Content(message2)))
            .map { it as ChatResponse }
            .reduce(aggregateAgentResponseFlow())
        println("Agent: ")
        println(agentAnswer2.message.value)

        val message3 = """
            Yes
        """.trimIndent()
        println("User: $message3")

        val agentAnswer3 = agentManager.execute(ChatRequest(chatId, Content(message3)))
            .map { it as ChatResponse }
            .reduce(aggregateAgentResponseFlow())
        println("Agent: ")
        println(agentAnswer3.message.value)
    }

    @Test
    fun `create a reservation streaming chat`() = runBlocking {

        val chatId = newId()

        val message1 = """
            I would like to create an reservation from 2024-10-12 12:00 to 2024-10-12 14:00
        """.trimIndent()
        println("User: $message1")

        println("Agent: ")
        val agentAnswer1 = reservationStreamingAgent.createReservation(chatId, message1).asFlow()
        agentAnswer1.collect { print(it) }

        println()

        val message2 = """
            customer id is 124231rwgerhy45
        """.trimIndent()
        println("User: $message2")

        println("Agent: ")

        val agentAnswer2 = reservationStreamingAgent.createReservation(chatId, message2).asFlow()
        agentAnswer2.collect { print(it) }

        val message3 = """
            Yes
        """.trimIndent()
        println("\nUser: $message3")

        println("Agent: ")
        val agentAnswer3 = reservationStreamingAgent.createReservation(chatId, message3).asFlow()
        agentAnswer3.collect { print(it) }
    }
}