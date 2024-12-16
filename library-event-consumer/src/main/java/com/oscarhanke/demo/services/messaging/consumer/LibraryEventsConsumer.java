package com.oscarhanke.demo.services.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")
@Component
@RequiredArgsConstructor
public class LibraryEventsConsumer {

    @KafkaListener(topics = "library-events", groupId = "group-1")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) {
        log.info("Kafka listener received message: {}", consumerRecord);
    }
}
