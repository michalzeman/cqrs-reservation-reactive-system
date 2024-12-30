package agent

import com.mz.customer.domain.api.CustomerDocument
import com.mz.ddd.common.api.domain.*
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerAccount
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerParam
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerTool
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.RegistrationAgent
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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled
@SpringBootTest(classes = [TestAiAgentConfiguration::class, OllamaLlmModelConfiguration::class, AgentAiServicesConfiguration::class, AiChatMemoryStorageConfiguration::class])
@ActiveProfiles("ollama", "test-ai")
class CustomerAgentTest {

    @Autowired
    private lateinit var agentManager: AgentManager

    @Autowired
    lateinit var registrationAgent: RegistrationAgent

    @Autowired
    lateinit var customerToolMock: CustomerTool

    @BeforeEach
    fun setUp() {
        Mockito.reset(customerToolMock)
    }

    @Test
    fun `AgentManager, register new user`(): Unit = runBlocking {
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
            customerAccount.toString()
        }

        agentManager.execute(agentRequest).collect {
            print("${it.message.value} ")
        }

        verify(customerToolMock).registerCustomer(customerParam)
    }

    @Test
    fun `AgentManager, register customer step by step`(): Unit = runBlocking {
        val chatId = newId()
        val message1 = """
            I want to register new customer
            - first name: Michal
        """.trimIndent()

        val request1 = NewChatRequest(Content(message1))

        println("User: ${request1.message.value}")
        val result1 = agentManager.execute(request1).map { it as ChatResponse }
            .reduce(aggregateAgentResponseFlow())

        println("Agent: ${result1.message.value}")

        val message2 = """
            last name is Zeman
        """.trimIndent()

        val request2 = ChatRequest(chatId, Content(message2))

        println("User: ${request2.message.value}")
        val result2 = agentManager.execute(request2).map { it as ChatResponse }
            .reduce(aggregateAgentResponseFlow())

        println("Agent: ${result2.message.value}")

        val message3 = """
            email is test@test.org
        """.trimIndent()

        val request3 = ChatRequest(chatId, Content(message3))

        println("User: ${request3.message.value}")
        val result3 = agentManager.execute(request3).map { it as ChatResponse }
            .reduce(aggregateAgentResponseFlow())

        val customerParam = CustomerParam("Michal", "Zeman", "test@test.org")

        val customerAccount = CustomerAccount(customerParam, newId().value).toString()

        whenever(customerToolMock.registerCustomer(
            CustomerParam("Michal", "Zeman", "test@test.org")))
            .thenAnswer { customerAccount.toString() }

        println("Agent: ${result3.message.value}")

        verify(customerToolMock).registerCustomer(customerParam)
    }

    @Test
    fun `CustomerAgent, load existing customer and classify customer is true`(): Unit = runBlocking {
        val id = Id("3d769d58-87a3-4c7c-9297-b85150dcb864")

        val customer: Customer = Customer(
            CustomerDocument(
                id,
                LastName("Zeman"),
                FirstName("Michal"),
                Email("test@test.org"),
                Version(1)
            )
        )

        val agentRequest = NewChatRequest(
            message = Content(
                """
                    My customer id is ${id.value}
                """.trimIndent()
            )
        )

        println("User: \n${agentRequest.message.value}")

        whenever(customerToolMock.findCustomer(id.value)).thenAnswer {
            customer.toString()
        }

        val responseFlow = agentManager.execute(agentRequest)
        val answer = responseFlow
            .map { it as ChatResponse }
            .reduce(aggregateAgentResponseFlow())

        println("Agent: ${answer.message.value}")

        assertThat(registrationAgent.isCustomerIdentified(answer.chatId)).isTrue()

        verify(customerToolMock).findCustomer(id.value)
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