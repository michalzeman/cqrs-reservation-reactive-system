package agent

import com.mz.customer.domain.api.CustomerDocument
import com.mz.ddd.common.api.domain.*
import com.mz.ddd.common.persistence.eventsourcing.aggregate.AggregateProcessor
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatCommand
import com.mz.reservationsystem.aiagent.domain.api.chat.ChatDocument
import com.mz.reservationsystem.aiagent.domain.chat.ChatApi
import com.mz.reservationsystem.aiagent.domain.chat.aggregate.*
import com.mz.reservationsystem.aiagent.domain.customer.Customer
import com.mz.reservationsystem.aiagent.domain.customer.CustomerRepository
import com.mz.reservationsystem.aiagent.domain.customer.RegisterCustomer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Primary

@TestConfiguration
@ComponentScan("com.mz.reservationsystem.aiagent.domain.ai.**")
@ComponentScan("com.mz.reservationsystem.aiagent.adapter.llm.**")
@ComponentScan("com.mz.reservationsystem.aiagent.adapter.customer.**")
class TestAiAgentConfiguration {

//    @Primary
//    @Bean
//    fun chatMemoryStore(): ChatMemoryStore {
//        return TestChatMemoryStore()
//    }

    @Primary
    @Bean
    fun chatApi(): ChatApi {
        return TestChatApi()
    }

//    @Primary
//    @Bean
    fun customerRepository(): CustomerRepository {
        return object : CustomerRepository {
            override suspend fun registerCustomer(registerCustomer: RegisterCustomer): Id = newId()
            override suspend fun findCustomer(id: Id): Customer = Customer(
                CustomerDocument(
                    aggregateId = newId(),
                    lastName = LastName("Doe"),
                    firstName = FirstName("John"),
                    email = Email("test@test.test"),
                    version = Version(1)
                )
            )
        }
    }

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