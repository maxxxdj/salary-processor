package com.dpdemo.salaryincreaser.models;

import lombok.*;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Employee {
    String firstName;
    String secondName;
    Position position;
    double salary;
}
