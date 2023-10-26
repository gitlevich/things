package example

import org.axonframework.commandhandling.GenericCommandMessage
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.*

class ThingServiceTwo(
    private val commandGateway: ReactorCommandGateway,
    private val queryGateway: ReactorQueryGateway,
    private val randomUUID: () -> UUID = { UUID.randomUUID() }
) {

    /**
     * Using FindThingByCorrelationId query
     */
    fun defineThing(thingSpec: ThingSpec): Mono<FindThingByCorrelationId.Response> {
        val commandMessage =
            GenericCommandMessage.asCommandMessage<Any>(DefineThingCommand(randomUUID(), thingSpec.purpose))
        return executeCommandAndAwaitFirstUpdate(
            FindThingByCorrelationId(commandMessage.identifier),
            FindThingByCorrelationId.Response::class.java,
            listOf(commandMessage)
        )
    }

    /**
     * Using FindThingById query
     */
    fun defineThing2(thingSpec: ThingSpec): Mono<FindThingById.Response> {
        val defineThingCommand = DefineThingCommand(randomUUID(), thingSpec.purpose)
        return executeCommandAndAwaitFirstUpdate(
            FindThingById(defineThingCommand.thingId),
            FindThingById.Response::class.java,
            listOf(GenericCommandMessage.asCommandMessage<Any>(defineThingCommand))
        )
    }

    /**
     * Like in my working implementation
     */
    private fun <LOCAL_RESULT> executeCommandAndAwaitFirstUpdate(
        query: Any,
        queryResultType: Class<LOCAL_RESULT>,
        commands: List<*>,
    ): Mono<LOCAL_RESULT> =
        queryGateway.subscriptionQuery(query, Any::class.java, queryResultType)
            .flatMap { result ->
                result.initialResult().then(
                    commandGateway
                        .sendAll(Flux.fromIterable(commands))
                        .then(result.updates().timeout(Duration.ofSeconds(5)).next())
                )
            }
}
