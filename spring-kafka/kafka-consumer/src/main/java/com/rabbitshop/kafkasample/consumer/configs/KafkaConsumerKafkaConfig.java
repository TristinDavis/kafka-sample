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
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * @EnableKafka annotation is required on the configuration class to enable detection of @KafkaListener annotation on spring managed beans.
 * <p>
 * For consuming messages, we need to configure a ConsumerFactory and a KafkaListenerContainerFactory. Once these beans are available in spring bean factory,
 * POJO based consumers can be configured using @KafkaListener annotation.
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Configuration(value = "kafkaConsumerKafkaConfig")
@Order(11)
@EnableKafka
public class KafkaConsumerKafkaConfig {

	@Resource(name = "kafkaConsumerConfig")
	KafkaConsumerConfig kafkaConsumerConfig;

	@Bean(name = "inventoryKafkaListenerContainerFactory")
	@Resource(name = "jsonObjectMapper")
	protected ConcurrentKafkaListenerContainerFactory<String, InventoryMsg> createInventoryKafkaListenerContainerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating inventory Kafka concurrent listener container factory...");

		final ConcurrentKafkaListenerContainerFactory<String, InventoryMsg> concKafkaListenerFactory = new ConcurrentKafkaListenerContainerFactory<>();
		concKafkaListenerFactory.setConsumerFactory(
				createInventoryKafkaConsumerFactory(jsonObjectMapper)
		);
		return concKafkaListenerFactory;
	}

	/**
	 * PLEASE NOTE: Creating the JsonDeserializer remember to specify to which targetType the byte[] should be deserialized to
	 */
	protected ConsumerFactory<String, InventoryMsg> createInventoryKafkaConsumerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating inventory Kafka consumer factory...");

		return new DefaultKafkaConsumerFactory(
				createBasicConsumerProperties(),
				new StringDeserializer(),
				createJsonDeserializer(jsonObjectMapper)
		);
	}

	protected Map<String, ?> createBasicConsumerProperties() {

		log.debug("Creating basic consumer properties...");

		final Map<String, Object> properties = new HashMap<>();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaConsumerConfig().getBootstrapServer());
		// allows a pool of processes to divide the work of consuming and processing records
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, getKafkaConsumerConfig().getGroupId());
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getKafkaConsumerConfig().getAutoOffsetReset());
		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		// properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		// properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		return properties;
	}

	protected Deserializer<?> createJsonDeserializer(final ObjectMapper jsonObjectMapper) {

		log.info("Creating json deserializer with ObjectMapper {}...", jsonObjectMapper);

		final Deserializer<?> jsonDeserializer = new JsonSerde(jsonObjectMapper).deserializer();
		jsonDeserializer.configure(
				createJsonDeserializerConfigs(),
				false
		);
		return jsonDeserializer;
	}

	protected Map<String, ?> createJsonDeserializerConfigs() {

		log.debug("Creating json deserializer configurations...");

		final Map<String, Object> configs = new HashMap<>();
		configs.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class);
		configs.put(JsonDeserializer.VALUE_DEFAULT_TYPE, InventoryMsg.class);
		configs.put(JsonDeserializer.TRUSTED_PACKAGES, getKafkaConsumerConfig().getJsonTrustedPackages());
		return configs;
	}

}
