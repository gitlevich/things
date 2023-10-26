# Subscription query for REST API

I have a REST client that wants, unlike me, to tell an endpoint to create an entity and block till it gets a response.
It wants the response to be an up-to-date representation of the entity. 

I have looked at https://github.com/AxonIQ/code-samples/tree/main/subscription-query-rest and ported it to Kotlin.
My implementation is in this project.

In this example:
- Command model: just a lonely [TheThing](src/main/kotlin/com/things/one/command/TheThing.kt) aggregate.
- Query model:
  - a [Thing](src/main/kotlin/com/things/one/query/Thing.kt) is what we are trying to define 
  - [ThingService](src/main/kotlin/com/things/one/query/ThingService.kt) is a literal port of the subscription query 
     Java example in the axon-examples project.
  - [ThingProjection](src/main/kotlin/com/things/one/query/ThingProjection.kt) is a projection.
  - [ThingController](src/main/kotlin/com/things/one/query/ThingController.kt) is a REST controller that uses the 
    ThingService to demonstrate the problem.

The service can be exercised in two ways:
- by running the test [ThingServiceTest](src/test/kotlin/com/things/one/query/ThingServiceTest.kt) that fails
  and is disabled to get the project to build.
- by hitting the REST endpoint with curl fails for both implementations as well (see below).

Both ways fail with this error:
```log
java.util.concurrent.TimeoutException: Did not observe any item or terminal signal within 5000ms in 'source(FluxConcatArray)' (and no fallback has been configured)
```

I need help to get either to work.

# To run:

In the project root execute:

```shell
`./run.sh`
```

# To test:

Axon example version (query by command correlation id):

```shell
curl -X "POST" "http://localhost:8080/api/thing/query/one" \
-H 'Content-Type: application/json' \
-d '{
  "purpose": "purpose"
}' 
```
 and 

Same, but tries to use another query, by aggregate id:

```shell
curl -X "POST" "http://localhost:8080/api/thing/query/two" \
-H 'Content-Type: application/json' \
-d '{
  "purpose": "purpose"
}' 
```
