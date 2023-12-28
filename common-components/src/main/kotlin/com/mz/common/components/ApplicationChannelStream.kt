package com.mz.common.components

import com.mz.ddd.common.api.domain.Message
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

/**
 * A data class that represents a message channel.
 * It encapsulates a Message object for transmission through a message channel.
 *
 * @property payload The Message object to be transmitted.
 */
data class ChannelMessage(val payload: Message) {

    private val completed: CompletableFuture<Boolean> = CompletableFuture()

    /**
     * Marks the message processing as successful by completing the CompletableFuture with a true value.
     */
    fun success() {
        completed.complete(true)
    }

    /**
     * Marks the message processing as failed by completing the CompletableFuture exceptionally with the provided error.
     *
     * @param error The Throwable that caused the failure.
     */
    fun failure(error: Throwable) {
        completed.completeExceptionally(error)
    }

    /**
     * Returns a Mono that represents the completion status of the message processing.
     * The Mono is created lazily when this method is called.
     *
     * @return A Mono<Boolean> that completes with a true value if the message processing was successful,
     * or completes exceptionally if the message processing failed.
     */
    fun processed(): Mono<Boolean> {
        return Mono.defer { Mono.fromFuture(completed) }
    }
}

/**
 * An interface that defines the contract for a message channel stream.
 */
interface ApplicationChannelStream {

    /**
     * Publishes a message to the channel.
     *
     * @param message The MessageChannel object to be published.
     */
    fun publish(message: ChannelMessage)

    fun messagesStream(): Flux<Message>

    /**
     * Subscribes to the message channel.
     * The handler function is invoked when a message is received on the channel.
     * The filter function is used to filter the messages received on the channel.
     *
     * @param handler A function that takes a Message object and returns a Mono<R>.
     * @param filter An optional function that takes a Message object and returns a Boolean.
     * @return A Disposable object which can be used to unsubscribe from the channel.
     */
    fun <M : Message, R> subscribeToChannel(
        messageClass: Class<M>,
        handler: (M) -> Mono<R>,
        filter: ((Message) -> Boolean)? = null
    ): Disposable

}

inline fun <reified M : Message> ApplicationChannelStream.subscribeToChannel(
    noinline handler: (M) -> Mono<Void>,
    noinline filter: ((Message) -> Boolean)? = null
): Disposable {
    return subscribeToChannel(M::class.java, handler, filter)
}