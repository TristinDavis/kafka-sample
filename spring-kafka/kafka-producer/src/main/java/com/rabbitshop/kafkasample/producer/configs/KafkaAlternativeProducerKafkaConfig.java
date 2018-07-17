package com.rabbitshop.kafkasample.producer.configs;

import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * SPRING-KAFKA DEFAULT CONFIGS - NOT WORKING
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
// @Configuration(value = "kafkaAlternativeProducerKafkaConfig")
@Order(10)
public class KafkaAlternativeProducerKafkaConfig {

	@Resource(name = "kafkaProducerConfig")
	KafkaProducerConfig kafkaProducerConfig;

	@Bean
	public Map<String, Object> producerConfigs() {

		final Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaProducerConfig().getBootstrapServers());
		props.put(ProducerConfig.CLIENT_ID_CONFIG, getKafkaProducerConfig().getClientId());
		props.put(ProducerConfig.RETRIES_CONFIG, getKafkaProducerConfig().getRetries());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return props;
	}

	@Bean
	public ProducerFactory<String, InventoryMsg> producerFactory() {

		// Transactional
		// final DefaultKafkaProducerFactory<String, InventoryMsg> defaultKafkaProducerFactory = new DefaultKafkaProducerFactory<>(producerConfigs());
		// defaultKafkaProducerFactory.setTransactionIdPrefix(getKafkaProducerConfig().getTransactionIdPrefix());
		// return defaultKafkaProducerFactory;

		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, InventoryMsg> kafkaTemplate() {

		final KafkaTemplate<String, InventoryMsg> kafkaTemplate = new KafkaTemplate<>(producerFactory());
		kafkaTemplate.setDefaultTopic(getKafkaProducerConfig().getDefaultTopicId());
		return kafkaTemplate;
	}

}
