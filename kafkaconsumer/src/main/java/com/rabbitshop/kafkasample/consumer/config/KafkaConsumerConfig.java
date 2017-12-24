package com.rabbitshop.kafkasample.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitshop.kafkasample.commons.messages.ComplexMsg;
import com.rabbitshop.kafkasample.commons.messages.NestedMsg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
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
@Configuration(value = "kafkaConsumerConfig")
@Order(2)
@EnableKafka
public class KafkaConsumerConfig {

	@Value(value = "${kafka.bootstrap.server}")
	@Getter
	String bootstrapServer;

	@Value(value = "${kafka.group.id}")
	@Getter
	String groupId;

	@Value(value = "${kafka.topic.simple.name}")
	@Getter
	String simpleTopicName;

	@Value(value = "${kafka.topic.complex.name}")
	@Getter
	String complexTopicName;

	@Value(value = "${kafka.topic.nested.name}")
	@Getter
	String nestedTopicName;

	@Value(value = "${kafka.countdown.latch.seconds}")
	@Getter
	int countDownLatchSeconds;

	@Value(value = "${kafka.auto.offset.reset}")
	@Getter
	String autoOffsetReset;

	@Value(value = "${kafka.json.trusted.packages}")
	@Getter
	String jsonTrustedPackages;

	protected ConsumerFactory<String, String> createSimpleKafkaConsumerFactory() {

		log.debug("Creating simple Kafka consumer factory...");

		return new DefaultKafkaConsumerFactory<>(
				createConsumerProps(StringDeserializer.class, StringDeserializer.class)
		);
	}

	/**
	 * PLEASE NOTE: Creating the JsonDeserializer remember to specify to which targetType the byte[] should be deserialized to
	 */
	protected ConsumerFactory<String, ComplexMsg> createComplexKafkaConsumerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating complex Kafka consumer factory...");

		return new DefaultKafkaConsumerFactory<String, ComplexMsg>(
				createBasicConsumerProps(), new StringDeserializer(), createJsonDeserializer(ComplexMsg.class, jsonObjectMapper)
		);
	}

	/**
	 * PLEASE NOTE: Creating the JsonDeserializer remember to specify to which targetType the byte[] should be deserialized to
	 */
	protected ConsumerFactory<String, NestedMsg> createNestedKafkaConsumerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating nested Kafka consumer factory...");

		return new DefaultKafkaConsumerFactory<String, NestedMsg>(
				createBasicConsumerProps(), new StringDeserializer(), createJsonDeserializer(NestedMsg.class, jsonObjectMapper)
		);
	}

	@Bean(name = "simpleKafkaListenerContainerFactory")
	public ConcurrentKafkaListenerContainerFactory<String, String> createSimpleKafkaListenerContainerFactory() {

		log.debug("Creating simple Kafka concurrent listener container factory...");

		final ConcurrentKafkaListenerContainerFactory<String, String> simpleConcKafkaListenerFactory = new ConcurrentKafkaListenerContainerFactory<>();
		simpleConcKafkaListenerFactory.setConsumerFactory(createSimpleKafkaConsumerFactory());
		return simpleConcKafkaListenerFactory;
	}

	@Bean(name = "complexKafkaListenerContainerFactory")
	@Resource(name = "jsonObjectMapper")
	public ConcurrentKafkaListenerContainerFactory<String, ComplexMsg> createComplexKafkaListenerContainerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating complex Kafka concurrent listener container factory...");

		final ConcurrentKafkaListenerContainerFactory<String, ComplexMsg> complexConcKafkaListenerFactory = new ConcurrentKafkaListenerContainerFactory<>();
		complexConcKafkaListenerFactory.setConsumerFactory(createComplexKafkaConsumerFactory(jsonObjectMapper));
		return complexConcKafkaListenerFactory;
	}

	@Bean(name = "nestedKafkaListenerContainerFactory")
	@Resource(name = "jsonObjectMapper")
	public ConcurrentKafkaListenerContainerFactory<String, NestedMsg> createNestedKafkaListenerContainerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating nested Kafka concurrent listener container factory...");

		final ConcurrentKafkaListenerContainerFactory<String, NestedMsg> nestedConcKafkaListenerFactory = new ConcurrentKafkaListenerContainerFactory<>();
		nestedConcKafkaListenerFactory.setConsumerFactory(createNestedKafkaConsumerFactory(jsonObjectMapper));
		return nestedConcKafkaListenerFactory;
	}

	protected Map<String, Object> createBasicConsumerProps() {

		final Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServer());
		// allows a pool of processes to divide the work of consuming and processing records
		props.put(ConsumerConfig.GROUP_ID_CONFIG, getGroupId());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getAutoOffsetReset());
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		// props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		// props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		return props;
	}

	protected <K extends Deserializer, V extends Deserializer> Map<String, Object> createConsumerProps(final Class<K> keyDeserializerClass, final Class<V> valueDeserializerClass) {

		final Map<String, Object> props = createBasicConsumerProps();
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);
		return props;
	}

	protected <V> JsonDeserializer<V> createJsonDeserializer(final Class<V> targetType, final ObjectMapper jsonObjectMapper) {

		log.info("JSON ObjectMapper {}", jsonObjectMapper);

		final JsonDeserializer<V> jsonDeserializer = new JsonDeserializer<>(targetType, jsonObjectMapper);
		jsonDeserializer.configure(createJsonDeserializerConfigs(), false);
		return jsonDeserializer;
	}

	protected Map<String, String> createJsonDeserializerConfigs() {

		final Map<String, String> configs = new HashMap<>();
		configs.put(JsonDeserializer.TRUSTED_PACKAGES, getJsonTrustedPackages());
		return configs;
	}

}
