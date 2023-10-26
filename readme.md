# Subscription query for REST API

I have a REST client that wants, unlike me, to tell an endpoint to create an entity and block till it gets a response.
It wants the response to be an up-to-date representation of the entity. 

I have looked at https://github.com/AxonIQ/code-samples/tree/main/subscription-query-rest and ported it to Kotlin.
My implementation is in this package, example.

In this example:
- a [Thing](src/main/kotlin/com/things/one/query/Thing.kt) is what we are trying to define 
- [ThingServiceOne](src/main/kotlin/com/things/one/query/ThingServiceOne.kt) is a literal port of the Java example in the axon-examples project. 
- [ThingServiceTwo](src/main/kotlin/com/things/one/query/ThingServiceTwo.kt) is the equivalent of my current implementation.

Neither works.

Tests are mixed with the code for compactness.

I would like help to get either to work.

This is the first time I implemented a projection properly, see [ThingProjection.kt](src/main/kotlin/com/things/one/query/ThingProjection.kt). For some
reason, while I have understood the concept for a long time, I never implemented it right with Axon. Weird.

Before, I just had **projectors** in each of my services that projected events to a local store, translating them to
the service-local model. I used subscription queries only in the case like this, but my implementation was somewhat
different, and it worked. See code below.

I would like to understand what I am doing wrong in the "Thing" implementation in the accompanying code. 

## Current working solution
Here is my current solution that I used successfully for a while. It works but is very verbose and unsightly.

```kotlin
interface SubscriptionQueryHandler<T> {

    @QueryHandler
    fun handleDumb(query: T): Optional<Unit> = Optional.empty<Unit>()

}
```

with implementation like
```kotlin
@Component
@ProcessingGroup("membership-api")
class CouponQueryHandler(private val repository: CouponRepository) :
    SubscriptionQueryHandler<LocalQueries.FindMember> {

    @QueryHandler
    fun handle(query: Queries.FindCoupon): CompletableFuture<Coupon> =
        CompletableFuture.supplyAsync { repository.findById(query.couponId) }
}

```

```kotlin
interface ServiceAdapter
    val commandGateway: ReactorCommandGateway
    val queryGateway: ReactorQueryGateway
    
    fun queryTimeout(): Duration = Duration.ofSeconds(5)
   
    fun <LOCAL_RESULT> executeCommandAndWaitForFirstUpdate(
        query: Any,
        queryResultType: Class<LOCAL_RESULT>,
        commands: List<*>,
    ): Mono<LOCAL_RESULT> =
        queryGateway.subscriptionQuery(query, Any::class.java, queryResultType)
            .flatMap { result ->
                result.initialResult().then(
                    commandGateway
                        .sendAll(Flux.fromIterable(commands))
                        .then(result.updates().timeout(queryTimeout()).next()))
            }
    ...
```
