package com.oscarhanke.demo.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.topic")
public record TopicProperties(
        String name,
        Integer partitions,
        Integer replicas
) {}
