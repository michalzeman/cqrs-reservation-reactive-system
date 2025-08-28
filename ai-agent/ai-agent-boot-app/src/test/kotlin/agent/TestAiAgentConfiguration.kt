package agent

import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateProcessor
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerIdentificationTool
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.customer.CustomerTool
import com.mz.reservationsystem.aiagent.adapter.llm.ai.chat.reservation.ReservationTool
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatCommand
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatDocument
import com.mz.reservationsystem.aiagent.application.chat.ChatApi
import com.mz.reservationsystem.aiagent.application.chat.aggregate.*
import org.mockito.kotlin.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Primary

@TestConfiguration
@ComponentScan("com.mz.reservationsystem.aiagent.domain.ai.**")
@ComponentScan("com.mz.reservationsystem.aiagent.adapter.llm.**")
class TestAiAgentConfiguration {

    @Primary
    @Bean
    fun chatApi(): ChatApi {
        return TestChatApi()
    }

    @Primary
    @Bean
    fun customerToolMock(): CustomerTool {
        return mock<CustomerTool>()
    }

    @Primary
    @Bean
    fun customerIdentificationToolMock(): CustomerIdentificationTool = mock<CustomerIdentificationTool>()

    @Primary
    @Bean
    fun reservationToolMocK() = mock<ReservationTool>()

}

class TestChatApi : ChatApi {

    private val aggregateProcessor = AggregateProcessor(ChatCommandHandler(), ChatEventHandler())

    private val storage: MutableMap<Id, Chat> = mutableMapOf()

    override suspend fun execute(cmd: ChatCommand): ChatDocument {
        return (storage[cmd.aggregateId] ?: cmd.aggregateId.getAggregate())
            .let { aggregateProcessor.execute(it, cmd) }
            .mapCatching {
                storage[cmd.aggregateId] = it.aggregate
                it.aggregate.toDocument(it.events.toSet())
            }.getOrThrow()
    }

    override suspend fun findById(id: Id): ChatDocument? {
        return storage[id]?.toDocument()
    }
}