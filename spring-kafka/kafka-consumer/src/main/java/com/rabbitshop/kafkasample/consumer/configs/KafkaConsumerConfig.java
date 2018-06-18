package com.rabbitshop.kafkasample.consumer.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

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
@Configuration(value = "kafkaConsumerConfig")
@Order(2)
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

	@Bean(name = "inventoryKafkaListenerContainerFactory")
	@Resource(name = "jsonObjectMapper")
	protected ConcurrentKafkaListenerContainerFactory<String, InventoryMsg> createInventoryKafkaListenerContainerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating inventory Kafka concurrent listener container factory...");

		final ConcurrentKafkaListenerContainerFactory<String, InventoryMsg> nestedConcKafkaListenerFactory = new ConcurrentKafkaListenerContainerFactory<>();
		nestedConcKafkaListenerFactory.setConsumerFactory(createInventoryKafkaConsumerFactory(jsonObjectMapper));
		return nestedConcKafkaListenerFactory;
	}

	/**
	 * PLEASE NOTE: Creating the JsonDeserializer remember to specify to which targetType the byte[] should be deserialised to
	 */
	protected ConsumerFactory<String, InventoryMsg> createInventoryKafkaConsumerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating inventory Kafka consumer factory...");

		return new DefaultKafkaConsumerFactory<String, InventoryMsg>(
				createBasicConsumerProperties(), new StringDeserializer(), createJsonDeserializer(InventoryMsg.class, jsonObjectMapper)
		);
	}

	protected Map<String, Object> createBasicConsumerProperties() {

		log.debug("Creating basic consumer properties...");

		final Map<String, Object> properties = new HashMap<>();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServer());
		// allows a pool of processes to divide the work of consuming and processing records
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, getGroupId());
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getAutoOffsetReset());
		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		// properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		// properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		return properties;
	}

	protected <V> JsonDeserializer<V> createJsonDeserializer(final Class<V> targetType, final ObjectMapper jsonObjectMapper) {

		log.info("Creating json deserializer with ObjectMapper {}...", jsonObjectMapper);

		final JsonDeserializer<V> jsonDeserializer = new JsonDeserializer<>(targetType, jsonObjectMapper);
		jsonDeserializer.configure(createJsonDeserializerConfigs(), false);
		return jsonDeserializer;
	}

	protected Map<String, String> createJsonDeserializerConfigs() {

		log.debug("Creating json deserializer configurations...");

		final Map<String, String> configs = new HashMap<>();
		configs.put(JsonDeserializer.TRUSTED_PACKAGES, getJsonTrustedPackages());
		return configs;
	}

}
