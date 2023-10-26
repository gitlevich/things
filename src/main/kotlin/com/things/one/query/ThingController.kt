package com.things.one.query

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping(path = ["/api/thing/query"], produces = [MediaType.APPLICATION_JSON_VALUE])
class ThingController(private val thingService: ThingService) {

    @PostMapping("/one")
    fun defineThingOneWay(@RequestBody spec: ThingSpec): Mono<Thing> =
        thingService.defineThing(spec)

    @PostMapping("/two")
    fun defineThingAnotherWay(@RequestBody spec: ThingSpec): Mono<Thing> =
        thingService.defineThing(spec)
}
