package com.rabbitshop.kafkasample.producer.configs;

import com.rabbitshop.kafkasample.producer.controllers.KafkaProducerOperation;
import com.rabbitshop.kafkasample.producer.converters.KafkaProducerOperationConverter;
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
import org.springframework.cloud.sleuth.instrument.async.TraceableScheduledExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Configuration(value = "kafkaProducerWebConfig")
@Order(20)
public class KafkaProducerWebConfig extends WebMvcConfigurationSupport {

	@Resource(name = "kafkaProducerOperationConverter")
	KafkaProducerOperationConverter kafkaProducerOperationConverter;

	@Resource(name = "kafkaProducerConfig")
	KafkaProducerConfig kafkaProducerConfig;

	@Autowired
	private BeanFactory beanFactory;

	@Override
	public FormattingConversionService mvcConversionService() {

		log.debug("Registering custom converters...");

		final FormattingConversionService formattingConversionService = super.mvcConversionService();
		formattingConversionService.addConverter(getKafkaProducerOperationConverter());
		return formattingConversionService;
	}

	// Version 1 - TaskScheduler (tracing not included, using TraceRunnable instead of normal Runnable)
	@Bean("kafkaProducerTaskScheduler")
	public ThreadPoolTaskScheduler createThreadPoolTaskScheduler() {

		log.debug("Creating ThreadPoolTaskScheduler with a size of {}", getKafkaProducerConfig().getThreadPoolSize());

		final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(getKafkaProducerConfig().getThreadPoolSize());
		threadPoolTaskScheduler.setThreadNamePrefix("kafka-producer");
		threadPoolTaskScheduler.initialize();
		return threadPoolTaskScheduler;
	}

	// Version 2 (uses Version 1) - ScheduledExecutorService (tracing already included)
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
