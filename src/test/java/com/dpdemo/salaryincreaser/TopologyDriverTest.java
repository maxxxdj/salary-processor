package com.dpdemo.salaryincreaser;

import com.dpdemo.salaryincreaser.configuration.AppConfig;
import com.dpdemo.salaryincreaser.exception.EntityNotPresentException;
import com.dpdemo.salaryincreaser.kafka.streams.TopologyProcessor;
import com.dpdemo.salaryincreaser.models.BonusProps;
import com.dpdemo.salaryincreaser.models.Employee;
import com.dpdemo.salaryincreaser.models.Position;
import com.dpdemo.salaryincreaser.service.calculation.SalaryCalculator;
import com.dpdemo.salaryincreaser.service.calculation.SalaryCalculatorImpl;
import com.dpdemo.salaryincreaser.service.factory.SalaryProcessorFactoryProvider;
import com.dpdemo.salaryincreaser.service.strategy.DeveloperSalaryIncreaseStrategy;
import com.dpdemo.salaryincreaser.service.strategy.DirectorSalaryIncreaseStrategy;
import com.dpdemo.salaryincreaser.util.CustomDeserializer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TopologyDriverTest {
    private TestOutputTopic<String, Employee> outputTopic;
    private TestInputTopic<String, Employee> readTopic;

    private TopologyProcessor topologyProcessor;
    private MeterRegistry meterRegistry = new SimpleMeterRegistry();
    @Spy
    private StreamsBuilderFactoryBean bean;
    private final StreamsBuilder streamsBuilder = new StreamsBuilder();
    private KStream<String, Employee> employeeKStream;
    TopologyTestDriver employeesTopologyDriver;


    @BeforeEach
    @SuppressWarnings("resource")
    void beforeEach() {
        final HashMap<String, Object> cfg = getDefaultProperties();
        final Properties props = new Properties();
        props.putAll(cfg);
        final ProducerFactory<String, Object> kafkaConfig = new DefaultKafkaProducerFactory<>(cfg);

        AppConfig appConfig = new AppConfig(
                Map.of("developer", new BonusProps(5000.00 ,10.00),
                       "director", new BonusProps(8000.00, 8.00)));

        final SalaryCalculator salaryCalculator = new SalaryCalculatorImpl(appConfig);


        SalaryProcessorFactoryProvider enhancerFactoryProvider = new SalaryProcessorFactoryProvider(
                Map.of("developer", new DeveloperSalaryIncreaseStrategy(appConfig, salaryCalculator),
                       "director", new DirectorSalaryIncreaseStrategy(appConfig, salaryCalculator)));

        topologyProcessor = new TopologyProcessor("readTopic", "outputTopic",
                "testApp", meterRegistry, bean, kafkaConfig, enhancerFactoryProvider);
        ReflectionTestUtils.setField(topologyProcessor, "streamsBuilder", streamsBuilder);

        employeeKStream = streamsBuilder.stream("readTopic");
        ReflectionTestUtils.setField(topologyProcessor, "employeeKStream", employeeKStream);

        employeesTopologyDriver = new TopologyTestDriver(topologyProcessor.employeeSalaryEnhancerTopology(), props);

        JsonSerde<Employee> employeeJsonSerde = new JsonSerde<>();
        CustomDeserializer<Employee> employeeCustomDeserializer = new CustomDeserializer<>();

        readTopic = employeesTopologyDriver.createInputTopic(
                "readTopic",
                Serdes.String().serializer(),
                employeeJsonSerde.serializer());

        outputTopic = employeesTopologyDriver.createOutputTopic(
                "outputTopic",
                Serdes.String().deserializer(),
                employeeCustomDeserializer);
    }

    @AfterEach
    void after() {
        employeesTopologyDriver.close();
    }

    @Test
    void testDeveloper_WhenSentForRaise_ThenRaise() {
        final Employee employeeBeforeRaise = getEmployee("Martin", "Miloshev", 3500.00, Position.DEVELOPER);
        readTopic.pipeInput("1", employeeBeforeRaise);
        var output = outputTopic.readKeyValue();
        var employeeWithRaise = output.value;
        assertNotNull(employeeWithRaise);
        assertTrue(employeeBeforeRaise.getSalary()<employeeWithRaise.getSalary());
    }

    @Test
    void testDirector_WhenSentForRaise_ThenReduceSalaryToLimit() {
        final Employee employeeBeforeRaise = getEmployee("Martin", "Miloshev", 10000, Position.DIRECTOR);
        readTopic.pipeInput("1", employeeBeforeRaise);
        var output = outputTopic.readKeyValue();
        var employeeWithRaise = output.value;
        assertNotNull(employeeWithRaise);
        assertTrue(employeeBeforeRaise.getSalary() > employeeWithRaise.getSalary());
    }

    @Test
    void testWhenNoSuchStrategy_ThenThrows() {
        final Employee employeeBeforeRaise = getEmployee("Martin", "Miloshev", 5000, Position.TEAMLEAD);
        SalaryProcessorFactoryProvider enhancerFactoryProvider = new SalaryProcessorFactoryProvider(
                Map.of("developer", new DeveloperSalaryIncreaseStrategy(null, null),
                        "director", new DirectorSalaryIncreaseStrategy(null, null)));

        assertThrows(EntityNotPresentException.class,() -> enhancerFactoryProvider.getStrategyPerEmployee(employeeBeforeRaise.getPosition()));
    }


    private Employee getEmployee(final String firstName, final String secondName, final double salary, final Position position) {
        return Employee.builder()
                .firstName(firstName)
                .secondName(secondName)
                .position(position)
                .salary(salary)
                .build();
    }

    private static HashMap<String, Object> getDefaultProperties() {
        final HashMap<String, Object> cfg = new HashMap<>();
        cfg.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        cfg.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        cfg.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        cfg.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());
        cfg.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return cfg;
    }
}
