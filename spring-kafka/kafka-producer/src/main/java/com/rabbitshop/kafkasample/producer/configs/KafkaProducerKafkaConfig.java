package com.rabbitshop.kafkasample.producer.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Configuration(value = "kafkaProducerKafkaConfig")
@Order(11)
public class KafkaProducerKafkaConfig {

	@Resource(name = "kafkaProducerConfig")
	KafkaProducerConfig kafkaProducerConfig;

	/**
	 * To create messages, first, we need to configure a ProducerFactory which sets the strategy for creating
	 * Kafka Producer instances.
	 */
	@Bean("inventoryKafkaProducerFactory")
	@Resource(name = "jsonObjectMapper")
	protected ProducerFactory<String, InventoryMsg> createInventoryKafkaProducerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating inventory Kafka producer factory...");

		return new DefaultKafkaProducerFactory(
				createBasicProducerProperties(),
				new StringSerializer(),
				createJsonSerializer(jsonObjectMapper)
		);
	}

	/**
	 * Then we need a KafkaTemplate which wraps a Producer instance and provides convenience methods for sending
	 * messages to Kafka topics.
	 * <p>
	 * PLEASE NOTE:
	 * Producer instances are thread-safe and hence using a single instance throughout an application context will
	 * give higher performance. Consequently, KakfaTemplate instances are also thread-safe and use of one instance
	 * is recommended.
	 */
	@Bean("inventoryKafkaTemplate")
	@Resource(name = "inventoryKafkaProducerFactory")
	protected KafkaTemplate<String, InventoryMsg> createInventoryKafkaTemplate(final ProducerFactory<String, InventoryMsg> inventoryKafkaProducerFactory) {

		log.debug("Creating inventory Kafka Template...");

		return new KafkaTemplate<>(inventoryKafkaProducerFactory);
	}

	protected Map<String, ?> createBasicProducerProperties() {

		log.debug("Creating basic producer properties...");

		final Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaProducerConfig().getBootstrapServer());
		props.put(ProducerConfig.RETRIES_CONFIG, getKafkaProducerConfig().getRetries());
		// props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		// props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		// props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		return props;
	}

	protected Serializer<?> createJsonSerializer(final ObjectMapper jsonObjectMapper) {

		log.info("Creating json serializer with ObjectMapper {}...", jsonObjectMapper);

		final Serializer<?> jsonSerializer = new JsonSerde(jsonObjectMapper).serializer();
		jsonSerializer.configure(
				createJsonSerializerConfigs(),
				false
		);
		return jsonSerializer;
	}

	protected Map<String, ?> createJsonSerializerConfigs() {

		log.debug("Creating json serializer configurations...");

		final Map<String, Object> configs = new HashMap<>();
		// compatible just with spring-kafka version > 2.1.0
		configs.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, getKafkaProducerConfig().isAddTypeInfo());
		return configs;
	}

}
