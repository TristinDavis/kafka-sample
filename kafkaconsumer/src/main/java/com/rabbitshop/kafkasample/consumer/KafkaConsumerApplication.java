package com.rabbitshop.kafkasample.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class KafkaConsumerApplication {

	public static void main(final String[] args) {

		SpringApplication.run(KafkaConsumerApplication.class, args);
	}

}
