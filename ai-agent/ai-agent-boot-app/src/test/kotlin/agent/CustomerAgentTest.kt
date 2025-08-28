package agent

import com.mz.customer.domain.api.CustomerDocument
import com.mz.ddd.common.api.domain.*
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.*
import com.mz.reservationsystem.aiagent.adapter.llm.storage.AiChatMemoryStorageConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.wiring.AgentAiServicesConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.wiring.OllamaLlmModelConfiguration
import com.mz.reservationsystem.aiagent.domain.ai.AgentManager
import com.mz.reservationsystem.aiagent.domain.ai.model.ChatRequest
import com.mz.reservationsystem.aiagent.domain.ai.model.ChatResponse
import com.mz.reservationsystem.aiagent.domain.ai.model.NewChatRequest
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import com.mz.reservationsystem.aiagent.domain.customer.Customer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Tag("aiTest")
@SpringBootTest(classes = [TestAiAgentConfiguration::class, OllamaLlmModelConfiguration::class, AgentAiServicesConfiguration::class, AiChatMemoryStorageConfiguration::class])
@ActiveProfiles("ollama", "test-ai")
class CustomerAgentTest {

    @Autowired
    private lateinit var agentManager: AgentManager

    @Autowired
    lateinit var customerAgent: CustomerAgent

    @Autowired
    lateinit var customerToolMock: CustomerTool

    @Autowired
    lateinit var customerIdentificationToolMock: CustomerIdentificationTool

    @BeforeEach
    fun setUp() {
        Mockito.reset(customerToolMock, customerIdentificationToolMock)
    }

    @Test
    fun `AgentManager, register new user`(): Unit = runBlocking {
        val chat = buildTestChat { request -> agentManager.execute(request) }

        val customerId = newId()

        val customerParam = CustomerParam(
            lastName = "Zeman",
            firstName = "Michal",
            email =
                "test@test.com"
        )

        val customerAccount = CustomerAccount(customerParam, customerId.value).toString()

        whenever(
            customerToolMock.registerCustomer(
                customerParam
            )
        ).thenAnswer {
            customerAccount
        }

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

        val chatId = chat(agentRequest)

        val yesRequest = ChatRequest(
            chatId,
            Content("Yes")
        )

        chat(yesRequest)

        verify(customerToolMock).registerCustomer(customerParam)
    }

    @Test
    fun `AgentManager, register customer step by step`(): Unit = runBlocking {
        val chat = buildTestChat { request -> agentManager.execute(request) }

        val customerParam = CustomerParam("Michal", "Zeman", "test@test.org")

        val customerAccount = CustomerAccount(customerParam, newId().value).toString()
        whenever(customerToolMock.registerCustomer(customerParam))
            .thenAnswer { customerAccount }

        val message1 = """
            I want to register new customer
        """.trimIndent()

        val request1 = NewChatRequest(Content(message1))

        val chatId = chat(request1)

        val message2 = """
            first name is Michal
        """.trimIndent()

        val request2 = ChatRequest(chatId, Content(message2))

        chat(request2)

        val message3 = """
            last name is Zeman
        """.trimIndent()

        val request3 = ChatRequest(chatId, Content(message3))

        chat(request3)

        val message4 = """
            email is test@test.org
        """.trimIndent()

        val request4 = ChatRequest(chatId, Content(message4))
        chat(request4)

        val message5 = """
            Yes
        """.trimIndent()
        val request5 = ChatRequest(chatId, Content(message5))
        chat(request5)
        verify(customerToolMock).registerCustomer(customerParam)
    }
}