package com.oscarhanke.demo.repository;

import com.oscarhanke.demo.model.entity.LibraryEvent;
import org.springframework.data.repository.CrudRepository;

public interface LibraryEventsRepository extends CrudRepository<LibraryEvent, Integer> {
}
