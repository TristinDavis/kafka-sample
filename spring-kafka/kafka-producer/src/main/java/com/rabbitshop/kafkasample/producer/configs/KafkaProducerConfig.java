package com.rabbitshop.kafkasample.producer.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Configuration(value = "kafkaProducerConfig")
@Order(10)
public class KafkaProducerConfig {

	@Value(value = "${kafka.inventory.producer.threadpool.size:4}")
	int threadPoolSize;

	@Value(value = "${spring.kafka.bootstrap-servers}")
	List<String> bootstrapServers;

	@Value(value = "${spring.kafka.client-id}")
	String clientId;

	@Value(value = "${spring.kafka.template.default-topic}")
	String defaultTopicId;

	@Value(value = "${kafka.inventory.topic.id:}")
	String topicId;

	@Value(value = "${spring.kafka.producer.transaction-id-prefix:}")
	String transactionIdPrefix;

	@Value(value = "${spring.kafka.producer.retries}")
	int retries;

	@Value(value = "${kafka.inventory.add.type.info:false}")
	boolean addTypeInfo;

}
