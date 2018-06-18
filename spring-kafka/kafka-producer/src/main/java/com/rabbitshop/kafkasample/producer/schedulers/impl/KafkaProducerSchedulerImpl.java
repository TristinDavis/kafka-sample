package com.rabbitshop.kafkasample.producer.schedulers.impl;

import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import com.rabbitshop.kafkasample.commons.messages.InventoryOperation;
import com.rabbitshop.kafkasample.commons.messages.InventorySource;
import com.rabbitshop.kafkasample.commons.utils.UuidGenerationUtils;
import com.rabbitshop.kafkasample.producer.schedulers.KafkaProducerScheduler;
import com.rabbitshop.kafkasample.producer.services.KafkaProducerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Random;


@Slf4j
@Getter(value = AccessLevel.PROTECTED)
@Component("kafkaProducerScheduler")
public class KafkaProducerSchedulerImpl implements KafkaProducerScheduler {

	static final String PROD_ID = "PROD-00001A";

	static final String SOURCE_ID = "SOURCE-001";

	static final String EMPLOYEE_ID = "EMPL-ID-001";

	@Resource(name = "kafkaProducerService")
	KafkaProducerService kafkaProducerService;

	@Override
	@Scheduled(fixedRate = 10000)
	public void sendInventoryAddMessage() {

		log.debug("Sending inventory ADD msg...");

		sendInventory(InventoryOperation.ADD, getRandomQty());
	}

	@Override
	@Scheduled(fixedRate = 5000, initialDelay = 2000)
	public void sendInventorySubMessage() {

		log.debug("Sending inventory SUB msg...");

		sendInventory(InventoryOperation.SUB, getRandomQty());
	}

	@Override
	@Scheduled(fixedRate = 60000, initialDelay = 15000)
	public void sendInventoryDeleteMessage() {

		log.debug("Sending inventory DELETE msg...");

		sendInventory(InventoryOperation.DELETE_PROD_INV, 0L);
	}

	protected void sendInventory(final InventoryOperation operation, final long qty) {

		final InventoryMsg inventoryMsg = InventoryMsg.builder()
				.id(generateId())
				.productId(PROD_ID)
				.quantity(qty)
				.operation(operation)
				.source(
						InventorySource.builder()
								.id(SOURCE_ID)
								.employeeId(EMPLOYEE_ID)
								.build()
				)
				.dateTime(LocalDateTime.now())
				.build();

		getKafkaProducerService().sendInventoryMessage(inventoryMsg);
	}

	protected String generateId() {

		try {
			return UuidGenerationUtils.generateUuid("SHA-256", "UTF-8");
		} catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
			log.error("Error generating UUID, using simple Type 4...");
		}
		return UuidGenerationUtils.generateUuidType4().toString();
	}

	protected long getRandomQty() {

		return new Random().nextLong();
	}

}
