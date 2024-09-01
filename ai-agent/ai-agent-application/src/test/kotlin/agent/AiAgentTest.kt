package agent

import com.mz.ddd.common.api.domain.newId
import com.mz.reservationsystem.aiagent.adapter.llm.AgentAiServicesConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.OllamaLlmModelConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.ai.agent.AssistantAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.agent.RegistrationAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.agent.toFlow
import com.mz.reservationsystem.aiagent.domain.ai.AgentManager
import com.mz.reservationsystem.aiagent.domain.ai.ChatAgentTypeClassification
import com.mz.reservationsystem.aiagent.domain.ai.model.ChatRequest
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAgentType
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled
@SpringBootTest(classes = [TestAiAgentConfiguration::class, OllamaLlmModelConfiguration::class, AgentAiServicesConfiguration::class])
@ActiveProfiles("ollama", "test")
//@ActiveProfiles("open-ai", "test")
class AiAgentTest {

    @Autowired
    private lateinit var agentManager: AgentManager

    @Autowired
    private lateinit var chatAgentTypeClassification: ChatAgentTypeClassification

//    @Autowired
//    private lateinit var chatClassification: ChatClassification

    @Autowired
    private lateinit var registrationAgent: RegistrationAgent

    @Autowired
    private lateinit var assisten: AssistantAgent

    @Test
    fun `test for register customer tool chain`() {
//        val message = """
//            Register new customer
//            - first name: Michal
//            - last name: Zeman
//            - email: test@test.com
//        """.trimIndent()
//        Assertions.assertThat(chatClassification.isRelatedToReservationSystem(message)).isTrue()
        val chatId = newId()
        val message1 = """
            I want to register new customer
            - first name: Michal
        """.trimIndent()

        println("User: $message1")
        val result1 = registrationAgent.chat(chatId, message1)

        println("Agent: $result1")

        val message2 = """
            last name is Zeman and email is test@test.org
        """.trimIndent()
//        val message2 = """
//            last name is Zeman
//        """.trimIndent()

        println("User: $message2")
        val result2 = registrationAgent.chat(chatId, message2)

        println("Agent: $result2")

        val message3 = """
            email is test@test.org
        """.trimIndent()

        println("User: $message3")
        val result3 = registrationAgent.chat(chatId, message3)

        println("Agent: $result3")
    }

    @Test
    fun `test for register customer, using complex object tool chain`() {
        val message = """
            Register new customer
            - first name: Michal
            - last name: Zeman
            - email: test@test.com
        """.trimIndent()


        val chatId = newId()
//        Assertions.assertThat(chatClassification.relatedToReservationSystem(message).result).isTrue()

        val result = registrationAgent.chat(chatId, message)

        println(result)
    }

    @Test
    fun `test for tool chain, using streaming Agent`(): Unit = runBlocking {
        val result = assisten.chatStream(
            newId(), """
            Register new customer 
            - first name: Michal
            - last name: Zeman
            - email: test@test.com
        """.trimIndent()
        )

        val resultAsString: Flow<String> = result.toFlow()
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
}