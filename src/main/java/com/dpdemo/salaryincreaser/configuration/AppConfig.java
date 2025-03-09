package com.dpdemo.salaryincreaser.configuration;

import com.dpdemo.salaryincreaser.models.BonusProps;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "bonus")
@AllArgsConstructor
public class AppConfig {
    private Map<String, BonusProps> bonusProps;
}
