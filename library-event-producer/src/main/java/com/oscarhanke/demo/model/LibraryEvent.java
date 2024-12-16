package com.oscarhanke.demo.model;

import com.oscarhanke.demo.model.enums.LibraryEventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record LibraryEvent(

        Integer libraryEventId,

        LibraryEventType libraryEventType,

        @Valid
        @NotNull
        Book book
) {
}
