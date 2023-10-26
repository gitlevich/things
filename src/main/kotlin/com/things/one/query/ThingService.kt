package com.things.one.query

import com.things.one.api.DefineThingCommand
import com.things.one.api.FindThingByCorrelationId
import com.things.one.api.FindThingById
import org.axonframework.commandhandling.GenericCommandMessage
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.queryhandling.SubscriptionQueryResult
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.*

@Service
class ThingService(
    private val commandGateway: CommandGateway,
    private val queryGateway: QueryGateway,
    private val randomUUID: () -> UUID = { UUID.randomUUID() }
) {

    fun defineThing(thingSpec: ThingSpec): Mono<Thing> {
        val command = GenericCommandMessage.asCommandMessage<Any>(DefineThingCommand(randomUUID(), thingSpec.purpose))
        val query = FindThingByCorrelationId(command.identifier)
        val response = queryGateway.subscriptionQuery(query, Any::class.java, FindThingByCorrelationId.Response::class.java)

        return sendAndReturnUpdate(command, response)
            .flatMap { it.thing.toMono() }
    }

    fun defineThingDifferently(thingSpec: ThingSpec): Mono<Thing> {
        val command = DefineThingCommand(randomUUID(), thingSpec.purpose)
        val query = FindThingById(command.thingId)
        val response = queryGateway.subscriptionQuery(query, Any::class.java, FindThingById.Response::class.java)

        return sendAndReturnUpdate(command, response)
            .flatMap { it.thing.toMono() }
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
