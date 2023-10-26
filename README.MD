# Subscription query for REST API

I have a REST client that wants, unlike me, to tell an endpoint to create an entity and block till it gets a response.
It wants the response to be an up-to-date representation of the entity. 

I have looked at https://github.com/AxonIQ/code-samples/tree/main/subscription-query-rest and ported it to Kotlin.
My implementation is in this project.

In this example:
- a [Thing](src/main/kotlin/com/things/one/query/Thing.kt) is what we are trying to define 
- [ThingServiceOne](src/main/kotlin/com/things/one/query/ThingServiceOne.kt) is a literal port of the Java example in the axon-examples project. 
- [ThingServiceTwo](src/main/kotlin/com/things/one/query/ThingServiceTwo.kt) is the equivalent of my current implementation.

Neither works:
- the tests [ThingServiceOneTest](src/test/kotlin/com/things/one/query/ThingServiceOneTest.kt) 
     and [ThingServiceTwoTest](src/test/kotlin/com/things/one/query/ThingServiceTwoTest.kt) fail. Both are disabled
     for that reason.
- hitting the REST endpoint with curl fails for both implementations as well.

I would like help to get either to work.

This is the first time I implemented a projection properly, see [ThingProjection.kt](src/main/kotlin/com/things/one/query/ThingProjection.kt). For some
reason, while I have understood the concept for a long time, I never implemented it right with Axon. Weird.

Before, I just had **projectors** in each of my services that projected events to a local store, translating them to
the service-local model. I used subscription queries only in the case like this, but my implementation was somewhat
different, and it worked. [ThingsServiceOne](src/main/kotlin/com/things/one/query/ThingServiceOne.kt) is identical
to my working implementation. For some reason, I can't get it to work in this example.

I would like to understand what I am doing wrong in the "Thing" implementation in the accompanying code. 

# To run:

In the project root execute:

```shell
`./run.sh`
```

# To test:

Axon example version:

```shell
curl -X "POST" "http://localhost:8080/api/thing/query/one" \
-H 'Content-Type: application/json' \
-d '{
  "purpose": "purpose"
}' 
```
 and 

My old version:

```shell
curl -X "POST" "http://localhost:8080/api/thing/query/two" \
-H 'Content-Type: application/json' \
-d '{
  "purpose": "purpose"
}' 
```
