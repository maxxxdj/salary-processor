package com.dpdemo.salaryincreaser.service.calculation;

import com.dpdemo.salaryincreaser.configuration.AppConfig;
import com.dpdemo.salaryincreaser.exception.EntityNotPresentException;
import com.dpdemo.salaryincreaser.models.BonusProps;
import com.dpdemo.salaryincreaser.models.Position;
import com.dpdemo.salaryincreaser.models.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class SalaryCalculatorImpl implements SalaryCalculator {

    private final AppConfig appConfig;

    public SalaryCalculatorImpl(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public Employee getEmployeeWCalculatedSalary(final Employee employee) {
        employee.setSalary(this.calculateSalary(employee));
        return employee;
    }

    private Double calculateSalary(final Employee employee) {
        final BonusProps bonusProps =
                Optional.of(appConfig.getBonusProps().get(employee.getPosition().toString()))
                        .orElseThrow(()-> new EntityNotPresentException(
                                "Bonus configuration missing for position {}"));
        final double newSalary = employee.getSalary() * bonusProps.percentage();
        if (newSalary > bonusProps.salaryLimit()){
            log.warn("Employee {} {} already has hit the limit of salary - {}",
                    employee.getFirstName(), employee.getSecondName(),
                    bonusProps.salaryLimit());
            return bonusProps.salaryLimit();
        }
        return newSalary;
    }
}
