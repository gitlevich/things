package example

import org.axonframework.commandhandling.GenericCommandMessage
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.queryhandling.SubscriptionQueryResult
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.*

class ThingServiceOne(
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway,
    private val randomUUID: () -> UUID = { UUID.randomUUID() }
) {

    fun defineThingAnotherWay(thingSpec: ThingSpec): Mono<FindThingByCorrelationId.Response> {
        val command = GenericCommandMessage.asCommandMessage<Any>(DefineThingCommand(randomUUID(), thingSpec.purpose))
        val query = FindThingByCorrelationId(command.identifier)
        val response = queryGateway.subscriptionQuery(query, Unit::class.java, Thing::class.java)

        return sendAndReturnUpdate(command, response)
            .map { thing -> FindThingByCorrelationId.Response(thing)}
    }

    /**
     * Direct port of the Java code from the Axon samples project at
     * https://github.com/AxonIQ/code-samples/tree/main/subscription-query-rest
     */
    private fun <U> sendAndReturnUpdate(command: Any, result: SubscriptionQueryResult<*, U>): Mono<U> =
        Mono.`when`(result.initialResult())
            .then(commandGateway.send<Any>(command).toMono())
            .thenMany(result.updates())
            .timeout(Duration.ofSeconds(5))
            .next()
            .doFinally { result.cancel() }
}
