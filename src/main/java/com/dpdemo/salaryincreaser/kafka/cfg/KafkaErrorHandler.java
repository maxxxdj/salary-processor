package com.dpdemo.salaryincreaser.kafka.cfg;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.BackOff;

/**
 * Custom error handler for Kafka!!!
 *
 * @author martin.miloshev
 */
@Slf4j
@Component
public class KafkaErrorHandler extends DefaultErrorHandler implements CommonErrorHandler {

    public KafkaErrorHandler() {
    }

    public KafkaErrorHandler(final BackOff backOff) {
        super(backOff);
    }

    public KafkaErrorHandler(final ConsumerRecordRecoverer recoverer) {
        super(recoverer);
    }

    public KafkaErrorHandler(final ConsumerRecordRecoverer recoverer, final BackOff backOff) {
        super(recoverer, backOff);
    }

    @Override
    public boolean handleOne(final Exception thrownException, final ConsumerRecord<?, ?> record,
                             final Consumer<?, ?> consumer, final MessageListenerContainer container) {
        manageException(thrownException);
        return true;
    }

    @Override
    public void handleOtherException(final Exception thrownException, final Consumer<?, ?> consumer,
                                     final MessageListenerContainer container, final boolean batchListener) {
        manageException(thrownException);
    }

    private void manageException(final Exception ex) {
        log.error("Error polling message: {}", ex.getMessage());
    }
}
