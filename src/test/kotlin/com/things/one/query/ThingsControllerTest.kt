package com.things.one.query

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.kotlin.core.publisher.toMono
import java.util.UUID

@WebFluxTest(controllers = [ThingsController::class])
internal class ThingsControllerTest {

    @Autowired
    private lateinit var client: WebTestClient

    /**
     * Mock services.
     */
    @TestConfiguration
    class MockConfig {
        @Bean
        fun thingServiceOne() = mockk<ThingService>()
    }

    @Autowired
    private lateinit var thingService: ThingService

    private val thing = Thing(UUID.randomUUID(), "a purpose")
    private val thingSpec = ThingSpec(thing.purpose)

    @BeforeEach
    fun setup() {
        every { thingService.defineThing(thingSpec) } returns thing.toMono()
        every { thingService.defineThingDifferently(thingSpec) } returns thing.toMono()
    }

    @Test
    fun `defineThingOneWay returns a thing`() {
        val response = client.post().uri("/api/thing/query/one")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(thingSpec)
            .exchange()
            .expectStatus().isOk
            .expectBody(Thing::class.java)
            .returnResult()

        assertThat(response.responseBody).isNotNull
        assertThat(response.responseBody).isEqualTo(thing)
    }

    @Test
    fun `defineThingAnotherWay returns a thing`() {
        val response = client.post().uri("/api/thing/query/two")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(thingSpec)
            .exchange()
            .expectStatus().isOk
            .expectBody(Thing::class.java)
            .returnResult()

        assertThat(response.responseBody).isNotNull
        assertThat(response.responseBody).isEqualTo(thing)
    }
}
