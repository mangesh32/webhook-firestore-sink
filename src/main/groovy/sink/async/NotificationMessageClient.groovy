package sink.async

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.KafkaKey
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.context.annotation.Property
import io.micronaut.messaging.annotation.Body
import tech.skylo.avro.NotificationMessage

import javax.inject.Singleton

@Singleton
@KafkaClient(
        properties = [
                @Property(name = "key.serializer", value = 'org.apache.kafka.common.serialization.StringSerializer'),
                @Property(name = "value.serializer", value = 'io.confluent.kafka.serializers.KafkaAvroSerializer'),

                @Property(name = "schema.registry.url", value = '${schema.registry.url}'),
                @Property(name = "schema.registry.basic.auth.user.info", value = '${schema.registry.basic.auth.user.info}'),
                @Property(name = "basic.auth.credentials.source", value= '${schema.basic.auth.credentials.source}'),
                @Property(name = "client.id", value='firestore-webhook-service'),
                @Property(name = "ssl.endpoint.identification.algorithm", value='${schema.ssl.endpoint.identification.algorithm}'),
        ]
)
interface NotificationMessageClient {


    void sendNotificationMessage(@Topic String topic, @KafkaKey String key, @Body NotificationMessage message)

}