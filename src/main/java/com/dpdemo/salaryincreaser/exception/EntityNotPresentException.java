package com.dpdemo.salaryincreaser.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EntityNotPresentException extends RuntimeException {
    private String message;
}
