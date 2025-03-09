package ru.yandex.practicum.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "delivery.cost")
@Data
public class DeliveryCostConfig {
    private BigDecimal baseRate = BigDecimal.valueOf(5.0);
    private BigDecimal fragileMultiplier = BigDecimal.valueOf(0.2);
    private BigDecimal weightMultiplier = BigDecimal.valueOf(0.3);
    private BigDecimal volumeMultiplier = BigDecimal.valueOf(0.2);
    private BigDecimal addressMultiplier = BigDecimal.valueOf(0.2);
    private Map<String, BigDecimal> warehouseMultipliers = new HashMap<>();
}