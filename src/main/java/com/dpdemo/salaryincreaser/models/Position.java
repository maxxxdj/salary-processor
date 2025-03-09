package com.dpdemo.salaryincreaser.models;

public enum Position {
    DEVELOPER,
    TEAMLEAD,
    MANAGER,
    DIRECTOR;

    @Override
    public String toString(){
        return this.name().toLowerCase();
    }
}
