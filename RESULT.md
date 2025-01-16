## logs produces

```
2025-01-16 11:33:00,752 INFO  [io.sma.rea.mes.kafka] (smallrye-kafka-consumer-thread-1) SRMSG18224: Executing consumer revoked re-balance listener for group 'avro-serialization-dlq'
2025-01-16 11:33:01,981 INFO  [io.sma.rea.mes.kafka] (vert.x-eventloop-thread-1) SRMSG18256: Initialize record store for topic-partition 'movies-0' at position -1.
2025-01-16 11:33:02,113 ERROR [io.sma.rea.mes.provider] (vert.x-worker-thread-1) SRMSG00200: The method org.acme.kafka.ConsumedMovieResource#process has thrown an exception

Exception in ConsumedMovieResource.java:17
          15      @Incoming("movies-from-external-service-in")
          16      public void process(ConsumerRecord<String, Movie> data) {
        → 17          throw new RuntimeException("Something went wrong");
          18      }
          19  }: java.lang.RuntimeException: Something went wrong
        at org.acme.kafka.ConsumedMovieResource.process(ConsumedMovieResource.java:17)
        at org.acme.kafka.ConsumedMovieResource_ClientProxy.process(Unknown Source)
        at org.acme.kafka.ConsumedMovieResource_SmallRyeMessagingInvoker_process_8fdfba5826048821acdebc1754e7ca19b9a22659.invoke(Unknown Source)
        at io.smallrye.reactive.messaging.providers.AbstractMediator.lambda$invokeBlocking$15(AbstractMediator.java:191)
        at io.smallrye.context.impl.wrappers.SlowContextualSupplier.get(SlowContextualSupplier.java:21)
        at io.smallrye.mutiny.operators.uni.builders.UniCreateFromDeferredSupplier.subscribe(UniCreateFromDeferredSupplier.java:25)
        at io.smallrye.mutiny.operators.AbstractUni.subscribe(AbstractUni.java:36)
        at io.smallrye.mutiny.operators.uni.builders.UniCreateFromDeferredSupplier.subscribe(UniCreateFromDeferredSupplier.java:36)
        at io.smallrye.mutiny.operators.AbstractUni.subscribe(AbstractUni.java:36)
        at io.smallrye.mutiny.groups.UniSubscribe.withSubscriber(UniSubscribe.java:51)
        at io.smallrye.mutiny.groups.UniSubscribe.with(UniSubscribe.java:110)
        at io.smallrye.mutiny.groups.UniSubscribe.with(UniSubscribe.java:88)
        at io.vertx.mutiny.core.Context$1.handle(Context.java:171)
        at io.vertx.mutiny.core.Context$1.handle(Context.java:169)
        at io.vertx.core.impl.ContextImpl.lambda$executeBlocking$5(ContextImpl.java:205)
        at io.vertx.core.impl.ContextInternal.dispatch(ContextInternal.java:270)
        at io.vertx.core.impl.ContextImpl$1.execute(ContextImpl.java:221)
        at io.vertx.core.impl.WorkerTask.run(WorkerTask.java:56)
        at io.vertx.core.impl.TaskQueue.run(TaskQueue.java:81)
        at org.jboss.threads.ContextHandler$1.runWith(ContextHandler.java:18)
        at org.jboss.threads.EnhancedQueueExecutor$Task.doRunWith(EnhancedQueueExecutor.java:2675)
        at org.jboss.threads.EnhancedQueueExecutor$Task.run(EnhancedQueueExecutor.java:2654)
        at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1591)
        at org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:11)
        at org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:11)
        at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
        at java.base/java.lang.Thread.run(Thread.java:1570)


2025-01-16 11:33:02,135 INFO  [io.sma.rea.mes.kafka] (vert.x-eventloop-thread-1) SRMSG18202: A message sent to channel `movies-from-external-service-in` has been nacked, sending the record to a dead letter topic internal-service-movies-dlq
2025-01-16 11:33:03,194 INFO  [io.sma.rea.mes.kafka] (vert.x-eventloop-thread-0) SRMSG18256: Initialize record store for topic-partition 'internal-service-movies-dlq-0' at position -1.
2025-01-16 11:33:03,201 INFO  [org.acm.kaf.ConsumedMovieResource] (vert.x-worker-thread-1) Got movie from DLQ: null
```

## TOPIC movie

run from the host - because rpk does not work inside the container (advertised adresse use the host ip which is unavailable inside the container)

> rpk topic consume movies -X brokers=localhost:32806

```json
{
  "topic": "movies",
  "value": "\u0000\u0000\u0000\u0000\u0001\u001aThe Godfather\ufffd\u001e",
  "timestamp": 1737023577742,
  "partition": 0,
  "offset": 0
}
```

## TOPIC movie-dlq

> rpk topic consume internal-service-movies-dlq -X brokers=localhost:32806

```json
{
  "topic": "internal-service-movies-dlq",
  "headers": [
    {
      "key": "dead-letter-exception-class-name",
      "value": "java.lang.RuntimeException"
    },
    {
      "key": "dead-letter-reason",
      "value": "Something went wrong"
    },
    {
      "key": "dead-letter-topic",
      "value": "movies"
    },
    {
      "key": "dead-letter-partition",
      "value": "0"
    },
    {
      "key": "dead-letter-offset",
      "value": "0"
    }
  ],
  "timestamp": 1737023582150,
  "partition": 0,
  "offset": 0
}
```

## DEBUG

Missing serializable exception

## RESULT

- value is missing inside `internal-service-movies-dlq` message
- no log produced about `org.apache.kafka.common.errors.SerializationException`
