package com.mz.common.components.internal

import com.mz.common.components.ChannelMessage
import com.mz.ddd.common.api.domain.DomainEvent
import com.mz.ddd.common.api.domain.Id
import com.mz.ddd.common.api.domain.Message
import com.mz.ddd.common.api.domain.instantNow
import com.mz.ddd.common.api.domain.newId
import kotlinx.datetime.Instant
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.DisposableBean
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

data class TestMessage(
    override val correlationId: Id = newId(),
    override val createdAt: Instant = instantNow(),
    override val eventId: Id = newId()
) : DomainEvent

internal class ChannelMessageStreamImplTest {

    private lateinit var messageChannelStream: ApplicationChannelStreamImpl
    private lateinit var disposableBean: DisposableBean

    @BeforeEach
    fun setUp() {
        messageChannelStream = ApplicationChannelStreamImpl()
        disposableBean = Mockito.mock(DisposableBean::class.java)
    }

    @AfterEach
    fun tearDown() {
        messageChannelStream.destroy()
    }

    @Test
    fun `destroy should clear subscriptions`() {
        val handler = { _: Message -> Mono.empty<String>() }
        val filter = { _: Message -> true }

        val disposable = messageChannelStream.subscribeToChannel(TestMessage::class.java, handler, filter)
        messageChannelStream.destroy()

        Assertions.assertThat(disposable.isDisposed).isTrue()
    }

    @Test
    fun `subscribeToChannel should handle message success`() {
        val message = TestMessage()
        val channelMessage = ChannelMessage(message)

        val handler = { _: Message -> Mono.just("Success") }
        val filter = { msg: Message -> msg is TestMessage }

        messageChannelStream.publish(channelMessage)
        messageChannelStream.subscribeToChannel(TestMessage::class.java, handler, filter)

        StepVerifier.create(channelMessage.processed())
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `subscribeToChannel without filter should handle message success`() {
        val message = TestMessage()
        val channelMessage = ChannelMessage(message)

        val handler = { _: Message -> Mono.just("Success") }

        messageChannelStream.publish(channelMessage)
        messageChannelStream.subscribeToChannel(TestMessage::class.java, handler)

        StepVerifier.create(channelMessage.processed())
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `subscribeToChannel should handle message failure`() {
        val message = TestMessage()
        val channelMessage = ChannelMessage(message)

        val handler = { _: Message -> Mono.error<String>(RuntimeException("Failure")) }
        val filter = { msg: Message -> msg is TestMessage }

        messageChannelStream.publish(channelMessage)
        messageChannelStream.subscribeToChannel(TestMessage::class.java, handler, filter)

        StepVerifier.create(channelMessage.processed())
            .verifyError()
    }
}