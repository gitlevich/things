package com.things.one.command

import com.things.one.api.DefineThingCommand
import com.things.one.api.ThingDefinedEvent
import com.things.one.util.WithLoggingMessageInterceptor
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
class TheThing(): WithLoggingMessageInterceptor() {
    @AggregateIdentifier
    private lateinit var thingId: UUID
    private lateinit var purpose: String

    @CommandHandler
    constructor(command: DefineThingCommand) : this() {
        logCommand("Processing $command")
        applyEvent(ThingDefinedEvent(command.thingId, command.purpose))
    }

    @EventSourcingHandler
    fun on(event: ThingDefinedEvent) {
        thingId = event.thingId
        purpose = event.purpose
    }
}
