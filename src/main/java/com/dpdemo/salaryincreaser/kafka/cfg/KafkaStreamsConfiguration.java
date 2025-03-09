package com.dpdemo.salaryincreaser.kafka.cfg;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaJsonDeserializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Kafka avro configuration class. This configuration class is dependent on the application profiles.
 *
 * @author martin.miloshev
 * @project derived-markets
 */
@Configuration
@Slf4j
public class KafkaStreamsConfiguration<K, V> {

    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServer;
    @Value("${spring.kafka.properties.schema.registry.url}")
    String schemaRegistryURL;
    @Value("${spring.kafka.properties.sasl.jaas.config}")
    String saslJaasCfg;
    @Value("${spring.kafka.properties.sasl.mechanism}")
    String saslJMechanism;
    @Value("${spring.kafka.properties.security.protocol}")
    String securityProtocol;
    @Value("${spring.application.name}")
    String applicationId;

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    public KafkaStreamsConfiguration(final StreamsBuilderFactoryBean streamsBuilderFactoryBean) {
        this.streamsBuilderFactoryBean = streamsBuilderFactoryBean;
    }

    @Bean
    public KafkaAdmin kafkaAdmin(){
        return new KafkaAdmin(getDefaultConfig());
    }

    @Bean
    public ProducerFactory<K, V> producerAvroFactory() {
        final var configProps = getDefaultConfig();
        configProps.put(ProducerConfig.CLIENT_DNS_LOOKUP_CONFIG, "use_all_dns_ips");
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 10000000);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonDeserializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    private Map<String, Object> getDefaultConfig() {
        final Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        configProps.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryURL);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.dpdemo*");
        configProps.put(SaslConfigs.SASL_MECHANISM, saslJMechanism);
        configProps.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasCfg);
        configProps.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        return configProps;
    }
}
