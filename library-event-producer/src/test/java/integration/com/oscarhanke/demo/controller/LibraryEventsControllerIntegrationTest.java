package com.oscarhanke.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oscarhanke.demo.model.LibraryEvent;
import com.oscarhanke.demo.util.TestUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@EmbeddedKafka(topics = "library-events")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"
})
class LibraryEventsControllerIntegrationTest {

    private static final String LIBRARY_EVENT_ENDPOINT = "/v1/library-event";
    private static final String HEADER_CONTENT_TYPE = "content-type";
    private static final String KAFKA_GROUP_ID = "group-1";
    private static final String KAFKA_AUTO_COMMIT = "true";
    private static final String KAFKA_OFFSET_RESET = "latest";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ObjectMapper objectMapper;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        var configs = KafkaTestUtils.consumerProps(KAFKA_GROUP_ID, KAFKA_AUTO_COMMIT, embeddedKafkaBroker);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KAFKA_OFFSET_RESET);
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer())
                .createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void postLibraryEvent() {
        // Given
        var expectedRecordCount = 1;
        var libraryEvent = TestUtil.newLibraryEventRecordWithLibraryEventId();
        var httpHeaders = new HttpHeaders(MultiValueMap.fromSingleValue(Map.of(HEADER_CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));
        var httpEntity = new HttpEntity<>(libraryEvent, httpHeaders);

        // When
        var result = restTemplate
                .exchange(LIBRARY_EVENT_ENDPOINT, HttpMethod.POST, httpEntity, LibraryEvent.class);

        // Then
        var statusCode = result.getStatusCode();
        assertThat(statusCode).isEqualTo(HttpStatus.CREATED);

        var records = KafkaTestUtils.getRecords(consumer);
        assertThat(records.count()).isEqualTo(expectedRecordCount);

        records.forEach(record -> {
            LibraryEvent found = TestUtil.parseLibraryEventRecord(objectMapper, record.value());
            assertThat(found).isEqualTo(libraryEvent);
        });
    }
}