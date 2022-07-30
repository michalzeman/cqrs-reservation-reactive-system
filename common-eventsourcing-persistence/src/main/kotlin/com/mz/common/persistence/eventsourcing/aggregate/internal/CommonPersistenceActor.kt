package com.mz.common.persistence.eventsourcing.aggregate.internal

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.pattern.StatusReply
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.javadsl.CommandHandlerWithReply
import akka.persistence.typed.javadsl.EventHandler
import akka.persistence.typed.javadsl.EventSourcedBehaviorWithEnforcedReplies
import akka.persistence.typed.javadsl.ReplyEffect
import com.mz.reservation.common.api.domain.DomainCommand
import com.mz.reservation.common.api.domain.DomainEvent
import com.mz.reservation.common.api.domain.command.AggregateCommandHandler
import com.mz.reservation.common.api.domain.event.AggregateEventHandler
import com.mz.reservation.common.api.util.Failure
import com.mz.reservation.common.api.util.Success

data class Command<C : DomainCommand>(val domainCommand: C, val replyTo: ActorRef<StatusReply<CommandOutcome>>)
//data class Command<C : DomainCommand>(val domainCommand: C, val replyTo: ActorRef<CommandOutcome>)

data class CommandOutcome(val events: List<DomainEvent>)

class CommonPersistenceActor<C : DomainCommand, E : DomainEvent, S> private constructor(
    private val persistenceId: PersistenceId,
    private val aggregateCommandHandler: AggregateCommandHandler<S, C, E>,
    private val aggregateEventHandler: AggregateEventHandler<S, E>,
    private val aggregateFactor: (PersistenceId) -> S
) : EventSourcedBehaviorWithEnforcedReplies<Command<C>, E, S>(persistenceId) {

    companion object {
        fun <C : DomainCommand, E : DomainEvent, S> create(
            persistenceId: PersistenceId,
            aggregateCommandHandler: AggregateCommandHandler<S, C, E>,
            aggregateEventHandler: AggregateEventHandler<S, E>,
            aggregateFactor: (PersistenceId) -> S
        ): Behavior<Command<C>> {
            return CommonPersistenceActor(
                persistenceId,
                aggregateCommandHandler,
                aggregateEventHandler,
                aggregateFactor
            )
        }
    }

    override fun commandHandler(): CommandHandlerWithReply<Command<C>, E, S> {
        return newCommandHandlerWithReplyBuilder().forState { it is S }
            .onAnyCommand(this::onCommand)
    }

    override fun eventHandler(): EventHandler<S, E> {
        return newEventHandlerBuilder()
            .forState { it is S }
            .onAnyEvent(aggregateEventHandler::apply)
    }

    override fun emptyState(): S {
        return aggregateFactor(persistenceId)
    }

    private fun onCommand(aggregate: S, cmd: Command<C>): ReplyEffect<E, S> {
        return when (val effect = aggregateCommandHandler.execute(aggregate, cmd.domainCommand)) {
            is Success -> Effect()
                .persist(effect.result)
                .thenReply(cmd.replyTo) { StatusReply.success(CommandOutcome(effect.result)) }

            is Failure -> Effect().reply(cmd.replyTo, StatusReply.error(effect.exc))
        }
    }
}