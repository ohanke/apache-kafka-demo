package com.oscarhanke.demo;

import com.oscarhanke.demo.config.properties.TopicProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {
		TopicProperties.class
})
public class LibraryEventProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryEventProducerApplication.class, args);
	}

}
