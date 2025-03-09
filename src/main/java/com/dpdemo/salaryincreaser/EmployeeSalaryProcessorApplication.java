package com.dpdemo.salaryincreaser;

import com.dpdemo.salaryincreaser.configuration.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
@EnableConfigurationProperties(AppConfig.class)
@EnableKafka
public class EmployeeSalaryProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeSalaryProcessorApplication.class, args);
	}

}
