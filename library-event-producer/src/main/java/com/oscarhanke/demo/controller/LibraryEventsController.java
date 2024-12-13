package com.oscarhanke.demo.controller;

import com.oscarhanke.demo.model.LibraryEvent;
import com.oscarhanke.demo.service.messaging.producer.LibraryEventsProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequestMapping("/v1/library-event")
@RequiredArgsConstructor
public class LibraryEventsController {

    private final LibraryEventsProducer libraryEventsProducer;

    @PostMapping
    public ResponseEntity<LibraryEvent> postLibraryEvent(
            @RequestBody @Valid LibraryEvent libraryEvent
    ) {
        log.info("Received POST request with body: {}", libraryEvent.toString());

        libraryEventsProducer.sendBlocking(libraryEvent);

        return ResponseEntity.status(CREATED).body(libraryEvent);
    }
}
