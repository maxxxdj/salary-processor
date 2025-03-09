package com.dpdemo.salaryincreaser.service.factory;

import com.dpdemo.salaryincreaser.exception.EntityNotPresentException;
import com.dpdemo.salaryincreaser.models.Position;
import com.dpdemo.salaryincreaser.service.strategy.SalaryIncreaseStrategy;

import java.util.Map;

public abstract class SalaryProcessorFactory {

    private final Map<String, SalaryIncreaseStrategy> salaryIncreaseStrategiesByPosition;

    public SalaryProcessorFactory(final Map<String, SalaryIncreaseStrategy> salaryIncreaseStrategiesByPosition) {
        this.salaryIncreaseStrategiesByPosition = salaryIncreaseStrategiesByPosition;
    }

    public SalaryIncreaseStrategy getStrategyPerEmployee(final Position position){
        return this.salaryIncreaseStrategiesByPosition.values().stream()
                .filter(strategy -> position.toString().equals(strategy.getType()))
                .findFirst()
                .orElseThrow(() -> new EntityNotPresentException(String.format("No such salary strategy in factory for department %s", position.toString())));
    }

    public Map<String, SalaryIncreaseStrategy> getAllStrategies(){
        return this.salaryIncreaseStrategiesByPosition;
    }
}
