package com.oscarhanke.demo.model.entity;

import com.oscarhanke.demo.model.enums.LibraryEventType;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.EnumType.STRING;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryEvent {

    @Id
    @GeneratedValue
    private Integer libraryEventId;

    @Enumerated(STRING)
    private LibraryEventType libraryEventType;

    @OneToOne(mappedBy = "libraryEvent", cascade = MERGE) //TODO fix
    @ToString.Exclude
    private Book book;
}
