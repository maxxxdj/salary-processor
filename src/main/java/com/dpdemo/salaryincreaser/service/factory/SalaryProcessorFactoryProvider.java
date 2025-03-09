package com.dpdemo.salaryincreaser.service.factory;

import com.dpdemo.salaryincreaser.service.strategy.SalaryIncreaseStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SalaryProcessorFactoryProvider extends SalaryProcessorFactory {
    public SalaryProcessorFactoryProvider(Map<String, SalaryIncreaseStrategy> enhancersBySport) {
        super(enhancersBySport);
    }
}
