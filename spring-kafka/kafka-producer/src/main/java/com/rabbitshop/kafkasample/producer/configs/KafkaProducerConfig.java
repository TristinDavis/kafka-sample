package com.rabbitshop.kafkasample.producer.configs;

import com.rabbitshop.kafkasample.producer.controllers.KafkaProducerOperation;
import com.rabbitshop.kafkasample.producer.tasks.AbstractKafkaProducerRunnableTask;
import com.rabbitshop.kafkasample.producer.tasks.impl.KafkaProducerAddQtyRunnableTask;
import com.rabbitshop.kafkasample.producer.tasks.impl.KafkaProducerDelProdRunnableTask;
import com.rabbitshop.kafkasample.producer.tasks.impl.KafkaProducerSubQtyRunnableTask;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.instrument.async.TraceableScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Configuration(value = "kafkaProducerConfig")
@Order(10)
public class KafkaProducerConfig {

	@Value(value = "${kafka.producer.threadpool.size:4}")
	int threadPoolSize;

	@Value(value = "${kafka.bootstrap.server}")
	@Getter(value = AccessLevel.PROTECTED)
	String bootstrapServer;

	@Value(value = "${kafka.topic.id}")
	String topicId;

	@Value(value = "${kafka.retries}")
	int retries;

	@Value(value = "${kafka.add.type.info}")
	boolean addTypeInfo;

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

		return new TraceableScheduledExecutorService(
				getBeanFactory(),
				threadPoolTaskScheduler.getScheduledExecutor()
		);
	}

	@Bean("kafkaProducerRunnableTasksMap")
	@Autowired(required = false)
	public Map<KafkaProducerOperation, AbstractKafkaProducerRunnableTask> createKafkaProducerRunnableTasksMap(
			final KafkaProducerAddQtyRunnableTask addRunnableTask,
			final KafkaProducerSubQtyRunnableTask subRunnableTask,
			final KafkaProducerDelProdRunnableTask delRunnableTask) {

		log.debug("Creating Kafka producer RunnableTask map...");
		// log.debug("RunnableTask operation {} class {}", KafkaProducerOperation.ADD, addRunnableTask);
		// log.debug("RunnableTask operation {} class {}", KafkaProducerOperation.SUB, subRunnableTask);
		// log.debug("RunnableTask operation {} class {}", KafkaProducerOperation.DEL, delRunnableTask);

		final Map<KafkaProducerOperation, AbstractKafkaProducerRunnableTask> runnableTasksMap = new HashMap<>();
		runnableTasksMap.put(KafkaProducerOperation.ADD, addRunnableTask);
		runnableTasksMap.put(KafkaProducerOperation.SUB, subRunnableTask);
		runnableTasksMap.put(KafkaProducerOperation.DEL, delRunnableTask);
		return runnableTasksMap;
	}

}
