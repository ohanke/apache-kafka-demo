package com.oscarhanke.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oscarhanke.demo.model.entity.LibraryEvent;
import com.oscarhanke.demo.model.enums.LibraryEventType;
import com.oscarhanke.demo.repository.LibraryEventsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryEventsService {

    private final LibraryEventsRepository repository;
    private final ObjectMapper mapper;

    public void processRecord(ConsumerRecord<Integer, String> consumerRecord) {
        LibraryEvent event = convert(consumerRecord);
        log.info("Converted value: {}", event.toString());
        LibraryEventType eventType = event.getLibraryEventType();

        switch (eventType) {
            case NEW -> save(event);
            case UPDATE -> update(event);
            default -> log.info("Invalid event type: {}", eventType);
        }
    }

    private void save(LibraryEvent libraryEvent) {
        log.info("Persisting LibraryEvent.");

        libraryEvent.getBook().setLibraryEvent(libraryEvent);

        repository.save(libraryEvent);

        log.info("LibraryEvent persisted successfully.");
    }

    private void update(LibraryEvent libraryEvent) {
        log.info("Updating LibraryEvent");

        validate(libraryEvent);

        repository.save(libraryEvent);

        log.info("Updated LibraryEvent successfully");
    }

    private void validate(LibraryEvent libraryEvent) {
        if (isNull(libraryEvent.getLibraryEventId())) {
            throw new IllegalArgumentException("LibraryEvent ID is missing.");
        }

        Optional<LibraryEvent> event = repository.findById(libraryEvent.getLibraryEventId());
        if (event.isEmpty()) {
            throw new IllegalArgumentException("Not a valid LibraryEvent.");
        }

        log.info("LibraryEvent validated successfully");
    }

    private LibraryEvent convert(ConsumerRecord<Integer, String> consumerRecord) {
        try {
            return mapper.readValue(consumerRecord.value(), LibraryEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
