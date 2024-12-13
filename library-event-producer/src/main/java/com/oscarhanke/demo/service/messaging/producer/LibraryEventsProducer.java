package com.oscarhanke.demo.service.messaging.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oscarhanke.demo.config.properties.TopicProperties;
import com.oscarhanke.demo.model.LibraryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibraryEventsProducer {

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final TopicProperties topicProperties;
    private final ObjectMapper objectMapper;

    public void send(LibraryEvent event) {
        Integer key = event.libraryEventId();
        String data = toJsonString(event);
        String topic = topicProperties.name();

        log.info("Sending message. Key: {}, Data: {}, Topic: {}", key, data, topic);

        kafkaTemplate
                .send(topic, key, data)
                .whenComplete((result, throwable) -> {
                    if (!isNull(throwable)) {
                        handleError(key, data, throwable);
                    }

                    handleSuccess(key, data, result);
                });
    }

    public void sendProducerRecord(LibraryEvent event) {
        Integer key = event.libraryEventId();
        String data = toJsonString(event);
        String topic = topicProperties.name();
        List<RecordHeader> recordHeaders = List.of(new RecordHeader("event-source-key", "event-source-value".getBytes()));
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(topic, null, key, data, recordHeaders);

        kafkaTemplate
                .send(producerRecord)
                .whenComplete((result, throwable) -> {
                    if (!isNull(throwable)) {
                        handleError(key, data, throwable);
                    }

                    handleSuccess(key, data, result);
                });
    }

    private void handleError(Integer key, String data, Throwable throwable) {
        log.error("Failed to send message. Key: {}, Data: {}, : {}", key, data, throwable);
    }

    private void handleSuccess(Integer key, String data, SendResult<Integer, String> sendResult) {
        log.info("Message was send successfully. Key: {}, Data: {}, Topic: {}", key, data, sendResult.toString());
    }

    private String toJsonString(LibraryEvent value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Error converting LibraryEvent: {}, message: {}", value.toString(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
