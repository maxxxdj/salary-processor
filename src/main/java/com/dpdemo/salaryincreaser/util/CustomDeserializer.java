package com.dpdemo.salaryincreaser.util;

import org.springframework.kafka.support.serializer.JsonDeserializer;


public class CustomDeserializer<T> extends JsonDeserializer<T> {

    public CustomDeserializer(){
        super();
        super.typeMapper.addTrustedPackages("com.dpdemo.*");
    }
}
