package example

import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.annotation.MetaDataValue
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
import org.springframework.stereotype.Component

@ProcessingGroup("things")
@Component
class ThingProjection(val emitter: QueryUpdateEmitter) {
    private val things = mutableSetOf<Thing>()

    @QueryHandler
    fun handle(query: FindThingByCorrelationId): FindThingByCorrelationId.Response =
        FindThingByCorrelationId.Response(null)

    @QueryHandler
    fun handle(query: FindThingById): FindThingById.Response =
        FindThingById.Response(things.firstOrNull { it.id == query.thingId })

    @EventHandler
    fun on(event: ThingDefinedEvent, @MetaDataValue("correlationId") correlationId: String) {
        val thing = Thing(event.thingId, event.purpose)
        things.add(thing)

        emitter.emit(
            FindThingByCorrelationId::class.java,
            { query -> query.correlationId == correlationId },
            thing
        )

        emitter.emit(
            FindThingById::class.java,
            { query -> query.thingId == thing.id },
            thing
        )
    }
}
