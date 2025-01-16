package org.acme.kafka;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class ExternalServiceMovieProducer {
    private final Properties props = new Properties();

    public ExternalServiceMovieProducer(@ConfigProperty(name = "kafka.bootstrap.servers") String bootstrapServers,
                                        @ConfigProperty(name = "mp.messaging.connector.smallrye-kafka.apicurio.registry.url") String apicurioRegistryUrl) {
        props.put("bootstrap.servers", bootstrapServers.substring("OUTSIDE://".length()));
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", KafkaAvroSerializer.class.getName());
        final String schemaRegistryUrl = apicurioRegistryUrl.replace("apis/registry/v2", "apis/ccompat/v6");
        props.put("schema.registry.url", schemaRegistryUrl);
        // auto.register.schemas default value true => will create the Avro schema for movie-value subject
    }

    void onStartProduceMovie(@Observes StartupEvent startupEvent) throws ExecutionException, InterruptedException {
        try (KafkaProducer<String, Object> producer = new KafkaProducer<>(props)) {
            producer.send(new ProducerRecord<>("movies", null, new Movie("The Godfather", 1972))).get();
        }
    }
}
