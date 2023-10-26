package example

import java.util.*

val thingSpec = ThingSpec(purpose = "purpose")
val thing = Thing(id = UUID.randomUUID(), purpose = thingSpec.purpose)
