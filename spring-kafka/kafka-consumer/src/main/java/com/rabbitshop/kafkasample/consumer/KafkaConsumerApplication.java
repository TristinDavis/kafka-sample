package com.rabbitshop.kafkasample.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


/**
 * PLEASE NOTE:
 * . Spring Boot performances: remove @SpringBootApplication, replace with @EnableAutoConfiguration and @ComponentScan
 * . Important: We need to include the com.fasterxml.jackson.core:jackson-databind dependency for working with rich header values.
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.rabbitshop.kafkasample"})
public class KafkaConsumerApplication {

	public static void main(final String[] args) {

		SpringApplication.run(KafkaConsumerApplication.class, args);
	}

}
