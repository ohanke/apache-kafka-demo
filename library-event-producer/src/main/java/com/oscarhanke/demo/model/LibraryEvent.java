package com.oscarhanke.demo.model;

import com.oscarhanke.demo.model.enums.LibraryEventType;

public record LibraryEvent(
        Integer libraryEventId,
        LibraryEventType libraryEventType,
        Book book
) {
}
