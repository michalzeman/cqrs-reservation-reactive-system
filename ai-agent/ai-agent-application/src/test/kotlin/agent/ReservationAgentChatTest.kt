package agent

import com.mz.ddd.common.api.domain.newId
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerTool
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationStreamingAgent
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationTool
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.TimeSlot
import com.mz.reservationsystem.aiagent.adapter.llm.storage.AiChatMemoryStorageConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.wiring.AgentAiServicesConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.wiring.OllamaLlmModelConfiguration
import com.mz.reservationsystem.aiagent.domain.ai.AgentManager
import com.mz.reservationsystem.aiagent.domain.ai.ChatAgentTypeClassification
import com.mz.reservationsystem.aiagent.domain.ai.model.*
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAgentType
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatterBuilder

@Tag("aiTest")
@SpringBootTest(classes = [TestAiAgentConfiguration::class, OllamaLlmModelConfiguration::class, AgentAiServicesConfiguration::class, AiChatMemoryStorageConfiguration::class])
@ActiveProfiles("ollama", "test-ai")
//@ActiveProfiles("open-ai", "test")
class ReservationAgentChatTest {

    @Autowired
    private lateinit var agentManager: AgentManager

    @Autowired
    lateinit var reservationStreamingAgent: ReservationStreamingAgent

    @Autowired
    private lateinit var chatAgentTypeClassification: ChatAgentTypeClassification

    @Autowired
    lateinit var reservationToolMock: ReservationTool

    @Autowired
    lateinit var customerToolMock: CustomerTool

    @BeforeEach
    fun setUp() {
        Mockito.reset(reservationToolMock, customerToolMock)
    }

    @Disabled
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
    fun `create a reservation chat`(): Unit = runBlocking {
        val customerId = newId()

        val customerData = Content(
            """
                {
                    "id": "${customerId.value}",
                    "first_name": "Michal",
                    "last_name": "Zeman",
                    "email": "michal.zeman@test.org"
                }
            """.trimIndent()
        )

        val startTimeText = "2024-10-12 12:00"
        val startTime: Instant = LocalDateTime.parse("2024-10-12T12:00:00", formatter).toInstant(ZoneOffset.UTC)
        val endTimeText = "2024-10-12 14:00"
        val endTime: Instant = startTime.plusSeconds(240)

        whenever(reservationToolMock.findTimeSlotByTimeWindow(
            argWhere { it.verifyStringDate() },
            argWhere { it.verifyStringDate() }
        )).thenAnswer {
            listOf(TimeSlot(startTime, endTime).toString())
        }

        whenever(reservationToolMock.createReservation(
            eq(customerId.value),
            argWhere { it.verifyStringDate() },
            argWhere { it.verifyStringDate() },
            eq("michal.zeman@test.org")
        )).thenAnswer { "Reservation has been created with the id: ${newId()}" }

        whenever(customerToolMock.findCustomer(anyString())).thenAnswer {
            customerData.value
        }

        val message1 = """
            I would like to create an reservation from $startTimeText to $endTimeText
        """.trimIndent()

        println("User: $message1")

        val agentAnswer1 = agentManager.execute(
            NewChatCustomerRequest(
                Content(message1),
                customerId,
                customerData = customerData
            )
        )
            .map { it as ChatResponse }
            .reduce(aggregateAgentResponseFlow())
        val chatId = agentAnswer1.chatId
        println("Agent: ")
        println(agentAnswer1.message.value)

//        val message2 = """
//            customer id is ${customerId.value}
//        """.trimIndent()
//        println("User: $message2")
//
//        val agentAnswer2 =
//            agentManager.execute(ChatCustomerRequest(Content(message2), chatId, customerId, customerData))
//                .map { it as ChatResponse }
//                .reduce(aggregateAgentResponseFlow())
//        println("Agent: ")
//        println(agentAnswer2.message.value)

        val message3 = """
            Yes
        """.trimIndent()
        println("User: $message3")

        val agentAnswer3 =
            agentManager.execute(ChatCustomerRequest(Content(message3), chatId, customerId, customerData))
                .map { it as ChatResponse }
                .reduce(aggregateAgentResponseFlow())
        println("Agent: ")
        println(agentAnswer3.message.value)

        verify(reservationToolMock, atLeastOnce()).findTimeSlotByTimeWindow(anyString(), anyString())
        verify(reservationToolMock, atLeastOnce()).createReservation(anyString(), anyString(), anyString(), anyString())
    }

    @Test
    fun `create a reservation chat, when customer is unknown`(): Unit = runBlocking {
        val customerId = newId()

        val customerData = Content(
            """
                {
                    "id": "${customerId.value}",
                    "first_name": "Michal",
                    "last_name": "Zeman",
                    "email": "michal.zeman@test.org"
                }
                """.trimIndent()
        )

        val startTimeText = "2024-10-12 12:00"
        val startTime: Instant = LocalDateTime.parse("2024-10-12T12:00:00", formatter).toInstant(ZoneOffset.UTC)
        val endTimeText = "2024-10-12 14:00"
        val endTime: Instant = startTime.plusSeconds(240)

        whenever(reservationToolMock.findTimeSlotByTimeWindow(
            argWhere { it.verifyStringDate() },
            argWhere { it.verifyStringDate() }
        )).thenAnswer {
            listOf(TimeSlot(startTime, endTime).toString())
        }

        whenever(reservationToolMock.createReservation(
            eq(customerId.value),
            argWhere { it.verifyStringDate() },
            argWhere { it.verifyStringDate() },
            eq("michal.zeman@test.org")
        )).thenAnswer { "Reservation has been created with the id: ${newId()}" }

        whenever(customerToolMock.findCustomer(anyString())).thenAnswer {
            customerData.value
        }

        val message1 = """
            I would like to create an reservation from $startTimeText to $endTimeText
        """.trimIndent()

        println("User: $message1")

        val agentAnswer1 = agentManager.execute(
            NewChatRequest(
                Content(message1)
            )
        )
            .map { it as ChatResponse }
            .reduce(aggregateAgentResponseFlow())
        val chatId = agentAnswer1.chatId
        println("Agent: ")
        println(agentAnswer1.message.value)

        val message2 = """
            customer id is ${customerId.value}
        """.trimIndent()
        println("User: $message2")

        val agentAnswer2 =
            agentManager.execute(ChatRequest(chatId, Content(message2)))
                .map { it as ChatResponse }
                .reduce(aggregateAgentResponseFlow())
        println("Agent: ")
        println(agentAnswer2.message.value)

        val message3 = """
            Yes
        """.trimIndent()
        println("User: $message3")

        val agentAnswer3 =
            agentManager.execute(ChatRequest(chatId, Content(message3)))
                .map { it as ChatResponse }
                .reduce(aggregateAgentResponseFlow())
        println("Agent: ")
        println(agentAnswer3.message.value)

        val message4 = """
            Yes make a reservation
        """.trimIndent()
        println("User: $message4")

        val agentAnswer4 =
            agentManager.execute(ChatRequest(chatId, Content(message4)))
                .map { it as ChatResponse }
                .reduce(aggregateAgentResponseFlow())
        println("Agent: ")
        println(agentAnswer4.message.value)

        verify(reservationToolMock).findTimeSlotByTimeWindow(anyString(), anyString())
        verify(reservationToolMock).createReservation(anyString(), anyString(), anyString(), anyString())
    }

    internal val formatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm")
        .optionalStart()
        .appendPattern(":ss")
        .optionalEnd()
        .optionalStart()
        .appendPattern("XXX")
        .optionalEnd()
        .toFormatter()

    internal fun String.verifyStringDate(): Boolean = runCatching {
        LocalDateTime.parse(this, formatter).toInstant(ZoneOffset.UTC)
    }.isSuccess
}