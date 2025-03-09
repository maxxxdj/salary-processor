package com.dpdemo.salaryincreaser.kafka.streams;

import com.dpdemo.salaryincreaser.models.Employee;
import com.dpdemo.salaryincreaser.service.factory.SalaryProcessorFactoryProvider;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TopologyProcessor {

    private final String readTopic;
    private final String produceTopic;
    private final String appName;
    final StreamsBuilderFactoryBean bean;
    private StreamsBuilder streamsBuilder;
    final ProducerFactory<String, Object> kafkaConfig;
    private KStream<String, Employee> employeeKStream;
    private final SalaryProcessorFactoryProvider salaryFactory;
    private final MeterRegistry meterRegistry;

    public TopologyProcessor(@Value("${app.kafka.topic.read-topic}") final String readTopic,
                             @Value("${app.kafka.topic.produce-topic}") final String produceTopic,
                             @Value("${spring.application.name}") final String appName,
                             final MeterRegistry meterRegistry,
                             final StreamsBuilderFactoryBean bean,
                             final ProducerFactory<String, Object> kafkaConfig,
                             final SalaryProcessorFactoryProvider salaryFactory) {
        this.readTopic = readTopic;
        this.produceTopic = produceTopic;
        this.appName = appName;
        this.bean = bean;
        this.kafkaConfig = kafkaConfig;
        this.salaryFactory = salaryFactory;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    void init() throws Exception {
        this.bean.setStateListener(((newState, oldState) -> log.info("Kafka streams state changed from {} to {}", oldState.name(), newState.name())));
        streamsBuilder = bean.getObject();
        employeeKStream = this.getEmployeeStream(streamsBuilder);
        this.employeeSalaryEnhancerTopology();
    }

    /**
     * A topology which will process Employees stream, increase their salary and send to @produceTopic.
     */
    public Topology employeeSalaryEnhancerTopology() {
        employeeKStream
                .mapValues(v -> salaryFactory.getStrategyPerEmployee(v.getPosition())
                        .increaseSalary(v))
                .peek((k, v) -> doCounterAndLog(meterRegistry, produceTopic, v))
                .to(produceTopic);
       return streamsBuilder.build();
    }

    private KStream<String, Employee> getEmployeeStream(final StreamsBuilder streamsBuilder) {
        JsonSerde<Employee> jsonSerde = new JsonSerde<>();
        return streamsBuilder.stream(readTopic, Consumed.with(Serdes.String(), jsonSerde));
    }

    private void doCounterAndLog(final MeterRegistry meterRegistry, final String topic, final Employee employee) {
        log.debug("Sending message to topic {} for employee {} {}", topic , employee.getFirstName(), employee.getSecondName());
            Counter.builder("my_custom_counter")
                    .tag("service_name", appName)
                    .tag("department", employee.getPosition().toString())
                    .tag("first_second_name", employee.getSecondName() + " " + employee.getSecondName())
                    .register(meterRegistry)
                    .increment();
        };
}
