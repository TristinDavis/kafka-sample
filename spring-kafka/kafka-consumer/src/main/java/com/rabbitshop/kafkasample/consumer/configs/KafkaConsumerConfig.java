package com.rabbitshop.kafkasample.consumer.configs;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.EnableKafka;


/**
 * @EnableKafka annotation is required on the configuration class to enable detection of @KafkaListener annotation on spring managed beans.
 * <p>
 * For consuming messages, we need to configure a ConsumerFactory and a KafkaListenerContainerFactory. Once these beans are available in spring bean factory,
 * POJO based consumers can be configured using @KafkaListener annotation.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Configuration(value = "kafkaConsumerConfig")
@Order(10)
@EnableKafka
public class KafkaConsumerConfig {

	@Value(value = "${kafka.bootstrap.server}")
	@Getter(value = AccessLevel.PROTECTED)
	String bootstrapServer;

	@Value(value = "${kafka.auto.offset.reset}")
	@Getter(value = AccessLevel.PROTECTED)
	String autoOffsetReset;

	@Value(value = "${kafka.json.trusted.packages}")
	@Getter(value = AccessLevel.PROTECTED)
	String jsonTrustedPackages;

	@Value(value = "${kafka.group.id}")
	String groupId;

	@Value(value = "${kafka.topic.id}")
	String topicId;

	@Value(value = "${kafka.countdown.latch.seconds}")
	int countDownLatchSeconds;

}
