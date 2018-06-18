package com.rabbitshop.kafkasample.producer.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import com.rabbitshop.kafkasample.producer.controllers.KafkaProducerOperation;
import com.rabbitshop.kafkasample.producer.tasks.AbstractKafkaProducerRunnableTask;
import com.rabbitshop.kafkasample.producer.tasks.impl.KafkaProducerAddQtyRunnableTask;
import com.rabbitshop.kafkasample.producer.tasks.impl.KafkaProducerDelProdRunnableTask;
import com.rabbitshop.kafkasample.producer.tasks.impl.KafkaProducerSubQtyRunnableTask;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.instrument.async.TraceableScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Configuration(value = "kafkaProducerConfig")
@Order(2)
public class KafkaProducerConfig {

	@Value(value = "${kafka.bootstrap.server}")
	@Getter(value = AccessLevel.PROTECTED)
	String bootstrapServer;

	@Value(value = "${kafka.topic.id}")
	String topicId;

	@Value(value = "${kafka.retries}")
	int retries;

	@Value(value = "${kafka.add.type.info}")
	boolean addTypeInfo;

	@Value(value = "${kafka.producer.threadpool.size:4}")
	int threadPoolSize;

	@Autowired
	private BeanFactory beanFactory;

	// Example 1 - TaskScheduler (tracing not included, using TraceRunnable instead of normal Runnable)
	@Bean("kafkaProducerTaskScheduler")
	public ThreadPoolTaskScheduler createThreadPoolTaskScheduler() {

		log.debug("Creating ThreadPoolTaskScheduler with a size of {}", getThreadPoolSize());

		final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(getThreadPoolSize());
		threadPoolTaskScheduler.setThreadNamePrefix("kafka-producer");
		threadPoolTaskScheduler.initialize();
		return threadPoolTaskScheduler;
	}

	// Example 2 - ScheduledExecutorService (tracing already included)
	@Bean("kafkaProducerScheduledExecutorService")
	@Resource(name = "kafkaProducerTaskScheduler")
	public TraceableScheduledExecutorService executor(final ThreadPoolTaskScheduler threadPoolTaskScheduler) {

		log.debug("Creating TraceableScheduledExecutorService...");

		return new TraceableScheduledExecutorService(getBeanFactory(), threadPoolTaskScheduler.getScheduledExecutor());
	}

	@Bean("kafkaProducerRunnableTasksMap")
	@Autowired(required = false)
	public Map<KafkaProducerOperation, AbstractKafkaProducerRunnableTask> createKafkaProducerRunnableTasksMap(
			final KafkaProducerAddQtyRunnableTask addRunnableTask,
			final KafkaProducerSubQtyRunnableTask subRunnableTask,
			final KafkaProducerDelProdRunnableTask delRunnableTask) {

		log.debug("Creating Kafka producer RunnableTask map...");
		log.debug("RunnableTask operation {} class {}", KafkaProducerOperation.ADD, addRunnableTask);
		log.debug("RunnableTask operation {} class {}", KafkaProducerOperation.SUB, subRunnableTask);
		log.debug("RunnableTask operation {} class {}", KafkaProducerOperation.DEL, delRunnableTask);

		final Map<KafkaProducerOperation, AbstractKafkaProducerRunnableTask> runnableTasksMap = new HashMap<>();
		runnableTasksMap.put(KafkaProducerOperation.ADD, addRunnableTask);
		runnableTasksMap.put(KafkaProducerOperation.SUB, subRunnableTask);
		runnableTasksMap.put(KafkaProducerOperation.DEL, delRunnableTask);
		return runnableTasksMap;
	}

	/**
	 * To create messages, first, we need to configure a ProducerFactory which sets the strategy for creating
	 * Kafka Producer instances.
	 */
	@Bean("inventoryKafkaProducerFactory")
	@Resource(name = "jsonObjectMapper")
	protected ProducerFactory<String, InventoryMsg> createInventoryKafkaProducerFactory(final ObjectMapper jsonObjectMapper) {

		log.debug("Creating inventory Kafka producer factory...");

		return new DefaultKafkaProducerFactory<>(
				createBasicProducerProperties(), new StringSerializer(), createJsonSerializer(jsonObjectMapper)
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

	protected Map<String, Object> createBasicProducerProperties() {

		log.debug("Creating basic producer properties...");

		final Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServer());
		props.put(ProducerConfig.RETRIES_CONFIG, getRetries());
		// props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		// props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		// props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		return props;
	}

	protected <V> JsonSerializer<V> createJsonSerializer(final ObjectMapper jsonObjectMapper) {

		log.info("Creating json serializer with ObjectMapper {}...", jsonObjectMapper);

		final JsonSerializer<V> jsonSerializer = new JsonSerializer<>(jsonObjectMapper);
		// compatible just with spring-kafka version > 2.1.0
		jsonSerializer.setAddTypeInfo(isAddTypeInfo());
		return jsonSerializer;
	}

}
