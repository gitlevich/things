package com.things.one.query

import com.things.one.query.Thing
import com.things.one.query.ThingSpec
import java.util.*

val thingSpec = ThingSpec(purpose = "purpose")
val thing = Thing(id = UUID.randomUUID(), purpose = thingSpec.purpose)
