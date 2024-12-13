package com.oscarhanke.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oscarhanke.demo.service.messaging.producer.LibraryEventsProducer;
import com.oscarhanke.demo.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LibraryEventsController.class)
public class LibraryEventsControllerUnitTest {

    private static final String LIBRARY_EVENT_ENDPOINT = "/v1/library-event";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LibraryEventsProducer libraryEventsProducer;

    @Test
    void postLibraryEvent() throws Exception {
        // Given
        var libraryEvent = TestUtil.libraryEventRecord();
        var json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventsProducer.sendBlocking(libraryEvent)).thenReturn(null);

        // When
        mockMvc
                .perform(post(LIBRARY_EVENT_ENDPOINT)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

        // Then
    }
}
