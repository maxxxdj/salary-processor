package com.dpdemo.salaryincreaser.kafka.cfg;

import com.teketik.spring.health.AsyncHealth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AsyncHealth(timeout = 30)
public class KafkaHealthIndicator implements HealthIndicator {

    private final StreamsBuilderFactoryBean bean;


    public KafkaHealthIndicator(final StreamsBuilderFactoryBean bean) {
        this.bean = bean;
    }


    @Override
    public Health getHealth(final boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        if (!isStreamsStateProperly()) {
            return Health.down().build();
        }
        return Health.up().build();
    }

    private boolean isStreamsStateProperly() {
        if (bean != null && bean.getKafkaStreams() != null) {
            var kafkaStreamsState = bean.getKafkaStreams().state();
            if (kafkaStreamsState.isShuttingDown()) {
                log.warn("Kafka streams state is {}, will change it", kafkaStreamsState.name());
                this.bean.setStateListener(((newState, oldState) -> log.info("[HEALTH] Kafka streams state changed from {} to {}", oldState.name(), newState.name())));
            }
            return kafkaStreamsState.isRunningOrRebalancing() || kafkaStreamsState.hasNotStarted();
        } else {
            return false;
        }
    }
}
