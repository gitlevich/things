package com.things.one.api

import com.things.one.query.Thing
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class DefineThingCommand(@TargetAggregateIdentifier val thingId: UUID, val purpose: String)
data class ThingDefinedEvent(val thingId: UUID, val purpose: String)

data class FindThingByCorrelationId(val correlationId: String) {
    data class Response(val thing: Thing?)
}

data class FindThingById(val thingId: UUID) {
    data class Response(val thing: Thing?)
}
