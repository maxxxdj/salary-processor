package com.dpdemo.salaryincreaser.service.calculation;

import com.dpdemo.salaryincreaser.models.Employee;

public interface SalaryCalculator {

    Employee getEmployeeWCalculatedSalary(final Employee employee);
}
