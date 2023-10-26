package example

import java.util.*

data class ThingDefinedEvent(val thingId: UUID, val purpose: String)
data class DefineThingCommand(val thingId: UUID, val purpose: String)

data class FindThingByCorrelationId(val correlationId: String) {
    data class Response(val thing: Thing?)
}

data class FindThingById(val thingId: UUID) {
    data class Response(val thing: Thing?)
}
