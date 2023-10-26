package com.things.one.command

import com.things.one.api.DefineThingCommand
import com.things.one.api.ThingDefinedEvent
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.Test
import java.util.*

class TheThingTest {
    private val experiment = AggregateTestFixture(TheThing::class.java)

    @Test
    fun `should publish ThingDefinedEvent on DefineThingCommand`() {
        experiment.givenNoPriorActivity()
            .`when`(defineThing)
            .expectEvents(thingDefined)
    }

    companion object {
        private val thingId = UUID.randomUUID()
        private val purpose = "To do something"
        private val defineThing = DefineThingCommand(thingId, purpose)
        private val thingDefined = ThingDefinedEvent(thingId, purpose)
    }
}
