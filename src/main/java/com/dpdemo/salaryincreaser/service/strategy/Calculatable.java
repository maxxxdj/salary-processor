package com.dpdemo.salaryincreaser.service.strategy;

import com.dpdemo.salaryincreaser.configuration.AppConfig;
import com.dpdemo.salaryincreaser.service.calculation.SalaryCalculator;
import org.springframework.stereotype.Component;

@Component
public abstract class Calculatable {

    protected final AppConfig appConfig;
    protected final SalaryCalculator salaryCalculator;

    protected Calculatable(AppConfig appConfig, SalaryCalculator salaryCalculator) {
        this.appConfig = appConfig;
        this.salaryCalculator = salaryCalculator;
    }

}
