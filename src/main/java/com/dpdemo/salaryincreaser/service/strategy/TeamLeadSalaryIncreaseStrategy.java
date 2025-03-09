package com.dpdemo.salaryincreaser.service.strategy;

import com.dpdemo.salaryincreaser.configuration.AppConfig;
import com.dpdemo.salaryincreaser.models.Employee;
import com.dpdemo.salaryincreaser.service.calculation.SalaryCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import static com.dpdemo.salaryincreaser.models.Position.TEAMLEAD;

@Slf4j
@Service
@ConditionalOnProperty(value = "team-lead-strategy", havingValue = "true")
public class TeamLeadSalaryIncreaseStrategy extends Calculatable implements SalaryIncreaseStrategy {

    protected TeamLeadSalaryIncreaseStrategy(AppConfig appConfig, SalaryCalculator salaryCalculator) {
        super(appConfig, salaryCalculator);
    }

    @Override
    public String getType() {
        return TEAMLEAD.toString();
    }

    @Override
    public Employee increaseSalary(final Employee employee) {
      return salaryCalculator.getEmployeeWCalculatedSalary(employee);
    }
}
