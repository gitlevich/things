package com.things.one.query

import com.things.one.api.FindThingByCorrelationId
import com.things.one.api.FindThingById
import io.mockk.every
import io.mockk.mockk
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.axonframework.queryhandling.SubscriptionQueryResult
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration

/**
 * I am trying to get the pattern described in the Axon samples project at
 * https://github.com/AxonIQ/code-samples/tree/main/subscription-query-rest to work in Kotlin.
 *
 * I am obviously doing something very wrong because this test fails with a timeout.
 */
@Disabled("Fails")
class ThingServiceTest {

    private val queryGateway: QueryGateway = mockk(relaxed = true)
    private val commandGateway: CommandGateway = mockk(relaxed = true)
    private val thingService = ThingService(commandGateway, queryGateway)

    @Test
    fun `subscribe-create-getUpdate sequence wanted by REST client should work according to Axon recipe - FindThingByCorrelationId`() {
        val subscriptionQueryResult =
            mockk<SubscriptionQueryResult<Any, FindThingByCorrelationId.Response>>(relaxed = true)
        every { subscriptionQueryResult.initialResult() } returns Mono.just(FindThingByCorrelationId.Response(null))
        every { subscriptionQueryResult.updates() } returns Flux.just(FindThingByCorrelationId.Response(thing))
        every {
            queryGateway.subscriptionQuery(
                any<FindThingByCorrelationId>(),
                Any::class.java,
                FindThingByCorrelationId.Response::class.java
            )
        } returns subscriptionQueryResult

        StepVerifier.create(thingService.defineThing(thingSpec))
            .expectSubscription()
            .expectNext(thing)
            .expectComplete()
            .verify(Duration.ofSeconds(5))
    }

    @Test
    fun `subscribe-create-getUpdate sequence wanted by REST client should work according to Axon recipe - FindThingById`() {
        val subscriptionQueryResult =
            mockk<SubscriptionQueryResult<Any, FindThingById.Response>>(relaxed = true)
        every { subscriptionQueryResult.initialResult() } returns Mono.just(FindThingById.Response(null))
        every { subscriptionQueryResult.updates() } returns Flux.just(FindThingById.Response(thing))
        every {
            queryGateway.subscriptionQuery(
                any<FindThingById>(),
                Any::class.java,
                FindThingById.Response::class.java
            )
        } returns subscriptionQueryResult

        StepVerifier.create(thingService.defineThingDifferently(thingSpec))
            .expectSubscription()
            .expectNext(thing)
            .expectComplete()
            .verify(Duration.ofSeconds(5))
    }
}
