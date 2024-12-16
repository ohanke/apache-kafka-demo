package com.oscarhanke.demo.service.messaging.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oscarhanke.demo.config.properties.TopicProperties;
import com.oscarhanke.demo.model.LibraryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibraryEventsProducer {

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final TopicProperties topicProperties;
    private final ObjectMapper objectMapper;

    public void sendAsync(LibraryEvent event) {
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

    public CompletableFuture<SendResult<Integer, String>> sendBlocking(LibraryEvent event) {
        Integer key = event.libraryEventId();
        String data = toJsonString(event);
        String topic = topicProperties.name();

        log.info("Sending message. Key: {}, Data: {}, Topic: {}", key, data, topic);

        return kafkaTemplate
                .send(topic, key, data)
                .whenComplete((result, throwable) -> {
                    if (!isNull(throwable)) {
                        handleError(key, data, throwable);
                    }

                    handleSuccess(key, data, result);
                });
    }

    public CompletableFuture<SendResult<Integer, String>> sendAsProducerRecord(LibraryEvent event) {
        Integer key = event.libraryEventId();
        String data = toJsonString(event);
        String topic = topicProperties.name();

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, data, topic);

        return kafkaTemplate.send(producerRecord);
    }


    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source-header-name", "event-source-header-value".getBytes()));

        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }

    private void handleError(Integer key, String data, Throwable throwable) {
        log.error("Failed to send message. Key: {}, Data: {}, : {}", key, data, throwable);
    }

    private void handleSuccess(Integer key, String data, SendResult<Integer, String> sendResult) {
        log.info("Message was send successfully. Key: {}, Data: {}, Result: {}", key, data, sendResult.toString());
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
