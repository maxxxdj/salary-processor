package com.dpdemo.salaryincreaser.service.strategy;

import com.dpdemo.salaryincreaser.models.Employee;
import org.springframework.stereotype.Service;

@Service
public interface SalaryIncreaseStrategy {
    String getType();
    Employee increaseSalary(final Employee input);
}
