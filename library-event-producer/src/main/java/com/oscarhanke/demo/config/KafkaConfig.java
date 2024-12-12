package com.oscarhanke.demo.config;

import com.oscarhanke.demo.config.properties.TopicProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final TopicProperties properties;

    @Bean
    public NewTopic libraryEvents() {
        return TopicBuilder
                .name(properties.name())
                .partitions(properties.partitions())
                .replicas(properties.replicas())
                .build();
    }
}
