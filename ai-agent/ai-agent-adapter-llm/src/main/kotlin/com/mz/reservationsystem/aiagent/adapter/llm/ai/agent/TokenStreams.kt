package com.mz.reservationsystem.aiagent.adapter.llm.ai.agent

import dev.langchain4j.service.TokenStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.asFlux
import org.apache.commons.logging.LogFactory
import reactor.core.publisher.Flux

private val logger = LogFactory.getLog(TokenStream::class.java)

/**
 * Convert [TokenStream] to [Flux]
 */
fun TokenStream.toFlux(): Flux<String> = this.toFlow().asFlux(Dispatchers.IO)

/**
 * Extension function to convert a [TokenStream] to a [Flow] of [String].
 * This function sets up a [Channel] to receive tokens from the [TokenStream] and
 * registers event handlers to send tokens to the channel, handle completion, and handle errors.
 *
 * @return a [Flow] of [String] representing the token stream.
 */
fun TokenStream.toFlow(): Flow<String> {
    val channel = Channel<String>()
    val tokenStream = this
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    channel.invokeOnClose { coroutineScope.cancel() }

    tokenStream.onNext { token ->
        coroutineScope.launch(Dispatchers.IO) { channel.send(token) }
    }.onComplete {
        logger.trace("Chat completed, message: $it")
        channel.close()
    }.onError {
        logger.error("onError -> ", it)
        channel.close(it)
    }.start()

    return channel.consumeAsFlow()
}
