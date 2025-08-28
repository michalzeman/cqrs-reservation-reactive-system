package agent

import com.mz.ddd.common.api.domain.newId
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerIdentificationTool
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerTool
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationTool
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.TimeSlot
import com.mz.reservationsystem.aiagent.adapter.llm.storage.AiChatMemoryStorageConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.wiring.AgentAiServicesConfiguration
import com.mz.reservationsystem.aiagent.adapter.llm.wiring.OllamaLlmModelConfiguration
import com.mz.reservationsystem.aiagent.application.ai.AgentManager
import com.mz.reservationsystem.aiagent.application.ai.ChatAgentTypeClassification
import com.mz.reservationsystem.aiagent.application.ai.model.ChatCustomerRequest
import com.mz.reservationsystem.aiagent.application.ai.model.ChatRequest
import com.mz.reservationsystem.aiagent.application.ai.model.NewChatCustomerRequest
import com.mz.reservationsystem.aiagent.application.ai.model.NewChatRequest
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatAgentType
import com.mz.reservationsystem.aiagent.domain.api.chat.Content
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
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
class ReservationAgentChatTest {

    @Autowired
    private lateinit var agentManager: AgentManager

    @Autowired
    private lateinit var chatAgentTypeClassification: ChatAgentTypeClassification

    @Autowired
    lateinit var reservationToolMock: ReservationTool

    @Autowired
    lateinit var customerToolMock: CustomerTool

    @Autowired
    lateinit var customerIdentificationToolMock: CustomerIdentificationTool

    @BeforeEach
    fun setUp() {
        Mockito.reset(reservationToolMock, customerToolMock, customerIdentificationToolMock)
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

        val chat = buildTestChat { request -> agentManager.execute(request) }

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

        whenever(
            reservationToolMock.findAvailableTimeSlotByTimeWindow(
                argWhere { it.verifyStringDate() },
                argWhere { it.verifyStringDate() }
            )).thenAnswer {
            listOf(TimeSlot(startTime, endTime).toString())
        }

        whenever(
            reservationToolMock.createReservation(
                eq(customerId.value),
                argWhere { it.verifyStringDate() },
                argWhere { it.verifyStringDate() },
                eq("michal.zeman@test.org")
            )
        ).thenAnswer { "Reservation has been created with the id: ${newId()}" }

        whenever(customerIdentificationToolMock.findCustomer(anyString())).thenAnswer {
            customerData.value
        }

        val message1 = """
            I would like to create an reservation from $startTimeText to $endTimeText
        """.trimIndent()

        val chatId = chat(
            NewChatCustomerRequest(
                Content(message1),
                customerId,
                customerData = customerData
            )
        )

        val message3 = """
            Yes
        """.trimIndent()
        println("User: $message3")

        chat(ChatCustomerRequest(Content(message3), chatId, customerId, customerData))

        verify(reservationToolMock, atLeastOnce()).findAvailableTimeSlotByTimeWindow(anyString(), anyString())
        verify(reservationToolMock, atLeastOnce()).createReservation(anyString(), anyString(), anyString(), anyString())
    }

    @Test
    fun `create a reservation chat, when customer is unknown`(): Unit = runBlocking {
        val chat = buildTestChat { request -> agentManager.execute(request) }
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

        whenever(
            reservationToolMock.findAvailableTimeSlotByTimeWindow(
                argWhere { it.verifyStringDate() },
                argWhere { it.verifyStringDate() }
            )).thenAnswer {
            listOf(TimeSlot(startTime, endTime).toString())
        }

        whenever(
            reservationToolMock.createReservation(
                eq(customerId.value),
                argWhere { it.verifyStringDate() },
                argWhere { it.verifyStringDate() },
                eq("michal.zeman@test.org")
            )
        ).thenAnswer { "Reservation has been created with the id: ${newId()}" }

        whenever(customerIdentificationToolMock.findCustomer(anyString())).thenAnswer {
            customerData.value
        }

        val message1 = """
            I would like to create an reservation from $startTimeText to $endTimeText
        """.trimIndent()

        val chatId = chat(
            NewChatRequest(
                Content(message1)
            )
        )

        val message2 = """
            customer id is ${customerId.value}
        """.trimIndent()

        chat(ChatRequest(chatId, Content(message2)))

        val message3 = """
            Yes
        """.trimIndent()
        chat(ChatRequest(chatId, Content(message3)))

        val message4 = """
            Yes make a reservation
        """.trimIndent()
        chat(ChatRequest(chatId, Content(message4)))

        verify(reservationToolMock).findAvailableTimeSlotByTimeWindow(anyString(), anyString())
        verify(reservationToolMock).createReservation(anyString(), anyString(), anyString(), anyString())
    }

    @Test
    fun `list all customer reservation, when customer has reservation, then are listed`(): Unit = runBlocking {
        val chat = buildTestChat { request -> agentManager.execute(request) }
        val customerId = newId()
        val firstName = "Michal"
        val lastName = "Zeman"
        val email = "michal.zeman@test.org"
        val customerData = Content(
            """
                {
                    "id": "${customerId.value}",
                    "first_name": "$firstName",
                    "last_name": "$lastName",
                    "email": "$email"
                }
                """.trimIndent()
        )

        val customerWithReservations = """
                {
                  "aggregateId": ${customerId},
                  "lastName": "$lastName",
                  "firstName": "$firstName",
                  "email": "$email",
                  "version": 2,
                  "reservations": [
                    {
                      "requestId": "6faf0ba2-607f-45e1-bc4e-7d9372e7de01",
                      "status": "CONFIRMED",
                      "reservationPeriod": {
                        "startTime": "2025-07-01T08:00:00Z",
                        "endTime": "2025-07-01T09:00:00Z"
                      },
                      "id": "577697b7-ddeb-4ce7-b217-1a1decb53f17"
                    }
                  ],
                  "docId": "dbc588c0-a2cd-4e31-a38b-a9b6209b3028",
                  "correlationId": "b4c8e473-e58b-4886-823f-27bc40af36ea",
                  "createdAt": "2025-01-18T16:34:31.247387594Z"
                }
            """.trimIndent()

        whenever(reservationToolMock.listAllCustomerReservation(customerId.value))
            .thenAnswer { customerWithReservations }

        whenever(customerIdentificationToolMock.findCustomer(customerId.value)).thenAnswer {
            customerData.value
        }

        val message = """
            I would like to see all my reservations
        """.trimIndent()
        val chatId = chat(
            NewChatCustomerRequest(
                Content(message),
                customerId,
                customerData = customerData
            )
        )

        verify(reservationToolMock).listAllCustomerReservation(customerId.value)
    }

    @Test
    fun `list all customer reservation for unknown, when customer has reservation, then are listed`(): Unit =
        runBlocking {
            val chat = buildTestChat { request -> agentManager.execute(request) }
            val customerId = newId()
            val firstName = "Michal"
            val lastName = "Zeman"
            val email = "michal.zeman@test.org"
            val customerData = Content(
                """
                {
                    "id": "${customerId.value}",
                    "first_name": "$firstName",
                    "last_name": "$lastName",
                    "email": "$email"
                }
                """.trimIndent()
            )

            val customerWithReservations = """
                {
                  "aggregateId": ${customerId},
                  "lastName": "$lastName",
                  "firstName": "$firstName",
                  "email": "$email",
                  "version": 2,
                  "reservations": [
                    {
                      "requestId": "6faf0ba2-607f-45e1-bc4e-7d9372e7de01",
                      "status": "CONFIRMED",
                      "reservationPeriod": {
                        "startTime": "2025-07-01T08:00:00Z",
                        "endTime": "2025-07-01T09:00:00Z"
                      },
                      "id": "577697b7-ddeb-4ce7-b217-1a1decb53f17"
                    }
                  ],
                  "docId": "dbc588c0-a2cd-4e31-a38b-a9b6209b3028",
                  "correlationId": "b4c8e473-e58b-4886-823f-27bc40af36ea",
                  "createdAt": "2025-01-18T16:34:31.247387594Z"
                }
            """.trimIndent()
            whenever(reservationToolMock.listAllCustomerReservation(customerId.value))
                .thenAnswer { customerWithReservations }

            whenever(customerIdentificationToolMock.findCustomer(customerId.value)).thenAnswer {
                customerData.value
            }

            val message1 = """
            I would like to see all my reservations
        """.trimIndent()

            val chatId = chat(
                NewChatRequest(
                    Content(message1)
                )
            )

            val message2 = """
            My id is ${customerId.value}
        """.trimIndent()
            chat(ChatRequest(chatId, Content(message2)))

            val message3 = """
            Yes
        """.trimIndent()
            chat(ChatRequest(chatId, Content(message3)))

            verify(customerIdentificationToolMock).findCustomer(customerId.value)
            verify(reservationToolMock).listAllCustomerReservation(customerId.value)
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