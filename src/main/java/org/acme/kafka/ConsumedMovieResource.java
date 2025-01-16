package org.acme.kafka;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class ConsumedMovieResource {
    @Incoming("internal-service-movies-dlq")
    public void processDlt(Movie movie) {
        Log.infov("Got movie from DLQ: {0}", movie);
    }

    @Incoming("movies-from-external-service-in")
    public void process(ConsumerRecord<String, Movie> data) {
        throw new RuntimeException("Something went wrong");
    }
}
