package com.dpdemo.salaryincreaser.kafka.cfg;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DeserializationExceptionHandler implements
    org.apache.kafka.streams.errors.DeserializationExceptionHandler {

  private final KafkaTemplate kafkaTemplate;

  private final String deadLetterTopic;

    public DeserializationExceptionHandler(final KafkaTemplate kafkaTemplate,
                                           @Value("${app.kafka.topic.dead-letter-topic}") final String deadLetterTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.deadLetterTopic = deadLetterTopic;
    }

    @Override
  public DeserializationHandlerResponse handle(final ProcessorContext context,
                                               final ConsumerRecord<byte[], byte[]> record, final Exception exception) {
    if (null != record.key() && null!= record.value()) {
      log.error(
              "Exception caught during Deserialization, will be sent to DLT topic - {}!",
              deadLetterTopic);
      kafkaTemplate.send(deadLetterTopic, record);
    }
    return DeserializationHandlerResponse.CONTINUE;
  }

  @Override
  public void configure(final Map<String, ?> configs) {
    // Kafka is not used
  }
}
