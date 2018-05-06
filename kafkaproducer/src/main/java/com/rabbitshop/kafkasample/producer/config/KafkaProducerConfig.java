package com.rabbitshop.kafkasample.producer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitshop.kafkasample.commons.messages.ComplexMsg;
import com.rabbitshop.kafkasample.commons.messages.NestedMsg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration(value = "kafkaProducerConfig")
@Order(2)
public class KafkaProducerConfig {

	@Value(value = "${kafka.bootstrap.server}")
	@Getter(value = AccessLevel.PROTECTED)
	String bootstrapServer;

	@Value(value = "${kafka.topic.simple.name}")
	@Getter
	String simpleTopicName;

	@Value(value = "${kafka.topic.complex.name}")
	@Getter
	String complexTopicName;

	@Value(value = "${kafka.topic.nested.name}")
	@Getter
	String nestedTopicName;

	/**
	 * To create messages, first, we need to configure a ProducerFactory which sets the strategy for creating
	 * Kafka Producer instances.
	 */
	@Bean(name = "simpleKafkaProducerFactory")
	public ProducerFactory<String, String> createSimpleKafkaProducerFactory() {

		log.debug("Creating simple Kafka producer factory...");

		return new DefaultKafkaProducerFactory<>(
				createProducerProps(StringSerializer.class, StringSerializer.class)
		);
	}

	@Bean(name = "complexKafkaProducerFactory")
	@Resource(name = "jsonObjectMapper")
	public ProducerFactory<String, ComplexMsg> createComplexKafkaProducerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating complex Kafka producer factory...");

		return new DefaultKafkaProducerFactory<>(
				createBasicProducerProps(), new StringSerializer(), createJsonSerializer(jsonObjectMapper)
		);
	}

	@Bean(name = "nestedKafkaProducerFactory")
	@Resource(name = "jsonObjectMapper")
	public ProducerFactory<String, NestedMsg> createNestedKafkaProducerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating nested Kafka producer factory...");

		return new DefaultKafkaProducerFactory<>(
				createBasicProducerProps(), new StringSerializer(), createJsonSerializer(jsonObjectMapper)
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
	@Bean(name = "simpleKafkaTemplate")
	@Resource(name = "simpleKafkaProducerFactory")
	public KafkaTemplate<String, String> createSimpleKafkaTemplate(final ProducerFactory<String, String> simpleKafkaProducerFactory) {

		log.debug("Creating simple Kafka Template...");

		return new KafkaTemplate<>(simpleKafkaProducerFactory);
	}

	@Bean(name = "complexKafkaTemplate")
	@Resource(name = "complexKafkaProducerFactory")
	public KafkaTemplate<String, ComplexMsg> createComplexKafkaTemplate(final ProducerFactory<String, ComplexMsg> complexKafkaProducerFactory) {

		log.debug("Creating complex Kafka Template...");

		return new KafkaTemplate<>(complexKafkaProducerFactory);
	}

	@Bean(name = "nestedKafkaTemplate")
	@Resource(name = "nestedKafkaProducerFactory")
	public KafkaTemplate<String, NestedMsg> createNestedKafkaTemplate(final ProducerFactory<String, NestedMsg> nestedKafkaProducerFactory) {

		log.debug("Creating nested Kafka Template...");

		return new KafkaTemplate<>(nestedKafkaProducerFactory);
	}

	protected Map<String, Object> createBasicProducerProps() {

		final Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServer());
		// props.put(ProducerConfig.RETRIES_CONFIG, 3);
		// props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		// props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		// props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		return props;
	}

	protected <K extends Serializer, V extends Serializer> Map<String, Object> createProducerProps(final Class<K> keySerializerClass, final Class<V> valueSerializerClass) {

		final Map<String, Object> props = createBasicProducerProps();
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializerClass);
		return props;
	}

	protected <V> JsonSerializer<V> createJsonSerializer(final ObjectMapper jsonObjectMapper) {

		log.info("JSON ObjectMapper {}", jsonObjectMapper);

		final JsonSerializer<V> jsonSerializer = new JsonSerializer<>(jsonObjectMapper);
		// compatible just with spring-kafka version > 2.1.0
		jsonSerializer.setAddTypeInfo(false);
		return jsonSerializer;
	}

}
