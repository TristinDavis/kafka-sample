package com.rabbitshop.kafkasample.consumer.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * SPRING-KAFKA DEFAULT CONFIGS - NOT WORKING
 *
 * @EnableKafka annotation is required on the configuration class to enable detection of @KafkaListener annotation on spring managed beans.
 * <p>
 * For consuming messages, we need to configure a ConsumerFactory and a KafkaListenerContainerFactory. Once these beans are available in spring bean factory,
 * POJO based consumers can be configured using @KafkaListener annotation.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
// @Configuration(value = "kafkaAlternativeConsumerKafkaConfig")
@Order(10)
// @EnableKafka
public class KafkaAlternativeConsumerKafkaConfig {

	@Resource(name = "kafkaConsumerConfig")
	KafkaConsumerConfig kafkaConsumerConfig;

	public Map<String, Object> consumerConfigs() {

		log.debug("Creating consumer configurations...");

		final Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaConsumerConfig().getBootstrapServers());
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, getKafkaConsumerConfig().getClientId());
		props.put(ConsumerConfig.GROUP_ID_CONFIG, getKafkaConsumerConfig().getGroupId());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getKafkaConsumerConfig().getAutoOffsetReset());
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		// props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		// props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		return props;
	}

	public Map<String, ?> deserializerConfigs() {

		log.debug("Creating json deserializer configurations...");

		final Map<String, Object> configs = new HashMap<>();
		configs.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class);
		configs.put(JsonDeserializer.VALUE_DEFAULT_TYPE, InventoryMsg.class);
		configs.put(JsonDeserializer.TRUSTED_PACKAGES, getKafkaConsumerConfig().getJsonTrustedPackages());
		return configs;
	}

	// public Deserializer<InventoryMsg> jsonDeserializer(final ObjectMapper jsonObjectMapper) {
	public Deserializer<?> jsonDeserializer(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating json deserializer with ObjMapper {}...", jsonObjectMapper);

		// final JsonDeserializer<InventoryMsg> jsonDeserializer = new JsonDeserializer<>(InventoryMsg.class);
		// final Deserializer<InventoryMsg> jsonDeserializer = new JsonSerde<>(InventoryMsg.class, jsonObjectMapper).deserializer();
		final Deserializer<?> jsonDeserializer = new JsonSerde(jsonObjectMapper).deserializer();
		jsonDeserializer.configure(deserializerConfigs(), false);
		return jsonDeserializer;
	}

	public ConsumerFactory<String, InventoryMsg> consumerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Loading consumer factory with ObjMapper {}...", jsonObjectMapper);

		// With default trusted packages: java.util, java.lang
		// return new DefaultKafkaConsumerFactory<>(consumerConfigs());

		// With custom trusted packages
		return new DefaultKafkaConsumerFactory(
				consumerConfigs(),
				new StringDeserializer(),
				jsonDeserializer(jsonObjectMapper)
		);
	}

	@Bean(name = "inventoryKafkaListenerContainerFactory")
	@Resource(name = "jsonObjectMapper")
	public ConcurrentKafkaListenerContainerFactory<String, InventoryMsg> kafkaListenerContainerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Loading kafka listener container factory with ObjMapper {}...", jsonObjectMapper);

		final ConcurrentKafkaListenerContainerFactory<String, InventoryMsg> kafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
		kafkaListenerContainerFactory.setConsumerFactory(consumerFactory(jsonObjectMapper));
		return kafkaListenerContainerFactory;
	}

}
