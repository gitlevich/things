package com.things.one.query

import example.*
import io.mockk.every
import io.mockk.mockk
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.axonframework.queryhandling.SubscriptionQueryResult
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
class ThingServiceTwoTest {
    private val queryGateway: ReactorQueryGateway = mockk(relaxed = true)
    private val commandGateway: ReactorCommandGateway = mockk(relaxed = true)
    private val thingService = ThingServiceTwo(commandGateway, queryGateway)

    @Test
    fun `subscribe-create-getUpdate sequence wanted by REST client should work`() {
        val subscriptionQueryResult =
            mockk<SubscriptionQueryResult<FindThingByCorrelationId.Response, FindThingByCorrelationId.Response>>(relaxed = true)
        every { subscriptionQueryResult.initialResult() } returns Mono.just(FindThingByCorrelationId.Response(null))
        every { subscriptionQueryResult.updates() } returns Flux.just(FindThingByCorrelationId.Response(thing))
        every {
            queryGateway.subscriptionQuery(
                any<FindThingByCorrelationId>(),
                FindThingByCorrelationId.Response::class.java,
                FindThingByCorrelationId.Response::class.java
            )
        } returns Mono.just(subscriptionQueryResult)

        StepVerifier.create(thingService.defineThing(thingSpec))
            .expectSubscription()
            .expectNext(FindThingByCorrelationId.Response(thing))
            .expectComplete()
            .verify(Duration.ofSeconds(5))
    }

    @Test
    fun `defineThing should subscribe to FindThingById, send command and return the first update`() {
        val subscriptionQueryResult =
            mockk<SubscriptionQueryResult<FindThingById.Response, FindThingById.Response>>(relaxed = true)
        every { subscriptionQueryResult.initialResult() } returns Mono.just(FindThingById.Response(null))
        every { subscriptionQueryResult.updates() } returns Flux.just(FindThingById.Response(thing))
        every {
            queryGateway.subscriptionQuery(
                any<FindThingById>(),
                FindThingById.Response::class.java,
                FindThingById.Response::class.java
            )
        } returns Mono.just(subscriptionQueryResult)

        StepVerifier.create(thingService.defineThing2(thingSpec))
            .expectSubscription()
            .expectNext(FindThingById.Response(thing))
            .expectComplete()
            .verify(Duration.ofSeconds(5))
    }
}
