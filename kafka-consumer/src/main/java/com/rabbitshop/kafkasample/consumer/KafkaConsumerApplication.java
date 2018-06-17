package com.rabbitshop.kafkasample.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


// Spring Boot performances: remove @SpringBootApplication, replace with @EnableAutoConfiguration and @ComponentScan
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.rabbitshop.kafkasample"})
public class KafkaConsumerApplication {

	public static void main(final String[] args) {

		SpringApplication.run(KafkaConsumerApplication.class, args);
	}

}
