package com.dpdemo.salaryincreaser.kafka.cfg;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ProducerExceptionHandler implements ProductionExceptionHandler {

    private final String deadLetterTopic;

    private final KafkaTemplate kafkaTemplate;

    public ProducerExceptionHandler(@Value("${app.kafka.topic.dead-letter-topic}") final String deadLetterTopic,
                                    final KafkaTemplate kafkaTemplate) {
        this.deadLetterTopic = deadLetterTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public ProductionExceptionHandlerResponse handle(final ProducerRecord<byte[], byte[]> record,
                                                     final Exception exception) {
        log.error("Exception occurred, will be sent to dlt! - {}", exception.getMessage());
        kafkaTemplate.send(deadLetterTopic, record);
        return ProductionExceptionHandlerResponse.CONTINUE;
    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
