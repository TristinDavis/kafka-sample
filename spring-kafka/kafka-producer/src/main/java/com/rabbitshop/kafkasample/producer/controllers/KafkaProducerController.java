package com.rabbitshop.kafkasample.producer.controllers;

import brave.Tracing;
import com.rabbitshop.kafkasample.producer.tasks.AbstractKafkaProducerRunnableTask;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.instrument.async.TraceRunnable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PROTECTED)
@RestController
@RequestMapping("/kafka/producer")
public class KafkaProducerController {

	final Map<KafkaProducerOperation, ScheduledFuture<?>> scheduledFuturesMap = new HashMap<>();

	@Resource(name = "kafkaProducerRunnableTasksMap")
	Map<KafkaProducerOperation, AbstractKafkaProducerRunnableTask> runnableTasksMap;

	// Version 1 - TaskScheduler + TraceRunnable
	@Resource(name = "kafkaProducerTaskScheduler")
	TaskScheduler taskScheduler;

	// Version 2 - TraceableScheduledExecutorService
	// @Resource(name = "kafkaProducerScheduledExecutorService")
	// ScheduledExecutorService scheduledExecutorService;

	@Autowired
	Tracing tracing;

	@Autowired
	SpanNamer spanNamer;

	/**
	 * Start sending messages to Kafka for specified operation
	 *
	 * @param operation
	 */
	@PostMapping("/start")
	@ResponseStatus(HttpStatus.OK)
	public void start(
			@RequestParam final KafkaProducerOperation operation) {

		log.debug("Request to start {} operation...", operation);

		if (getScheduledFuturesMap().get(operation) == null) {

			final AbstractKafkaProducerRunnableTask runnableTask = getRunnableTasksMap().get(operation);
			if (runnableTask != null) {
				scheduleTask(operation, runnableTask);

			} else {
				log.error("No RunnableTask found for operation {}", operation);
			}

		} else {
			log.warn("{} already started!", operation);
		}

	}

	/**
	 * Stop sending messages to Kafka for specified operation
	 *
	 * @param operation
	 */
	@PostMapping("/stop")
	@ResponseStatus(HttpStatus.OK)
	public void stop(
			@RequestParam final KafkaProducerOperation operation) {

		log.debug("Request to stop {}", operation);

		final ScheduledFuture<?> scheduledFuture = getScheduledFuturesMap().get(operation);
		if (scheduledFuture != null) {
			cancelTask(operation, scheduledFuture);

		} else {
			log.warn("{} not yet started!", operation);
		}
	}

	/**
	 * Return the total number of sent messages for the specified operation
	 *
	 * @param operation
	 *
	 * @return total sent message for specified operation
	 */
	@GetMapping("/total")
	public long getTotalSentMessages(
			@RequestParam final KafkaProducerOperation operation) {

		log.debug("Getting total sent messages for operation {}...", operation);

		final AbstractKafkaProducerRunnableTask runnableTask = getRunnableTasksMap().get(operation);
		if (runnableTask != null) {

			final long total = runnableTask.getTotalSentMessages();
			log.info("Total sent messages for operation {}: {}", operation, total);
			return total;
		}

		log.error("No RunnableTask found for operation {}", operation);
		return -1;
	}

	/**
	 * List all available operations
	 *
	 * @return list of operations
	 */
	@GetMapping("/operations")
	public List<String> getOperations() {

		log.debug("Available operations: {}", Arrays.toString(KafkaProducerOperation.values()));

		return Arrays.stream(
				KafkaProducerOperation.values())
				.map(KafkaProducerOperation::getValue)
				.collect(Collectors.toList());
	}

	protected void scheduleTask(final KafkaProducerOperation operation, final AbstractKafkaProducerRunnableTask runnableTask) {

		final long rate = runnableTask.getRate();
		log.info("Starting Kafka Producer {} with a rate of {} ms...", operation, rate);

		// Version 1 - TaskScheduler + TraceRunnable
		getScheduledFuturesMap().put(
				operation,
				getTaskScheduler().scheduleAtFixedRate(
						new TraceRunnable(getTracing(), getSpanNamer(), runnableTask),
						rate
				)
		);

		// Version 2 - TraceableScheduledExecutorService
		// getScheduledFuturesMap().put(
		// 		operation,
		// 		getScheduledExecutorService().scheduleAtFixedRate(runnableTask, 0L, rate, TimeUnit.MILLISECONDS)
		// );
	}

	protected void cancelTask(final KafkaProducerOperation operation, final ScheduledFuture<?> scheduledFuture) {

		log.info("Stopping Kafka Producer {}...", operation);
		if (scheduledFuture.cancel(false)) {
			getScheduledFuturesMap().remove(operation);
		} else {
			log.error("Stop and removal of {} operation failed :( try again later...", operation);
		}
	}

}
