package com.rabbitshop.kafkasample.producer.tasks;

import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import com.rabbitshop.kafkasample.commons.messages.InventoryOperation;
import com.rabbitshop.kafkasample.commons.messages.InventorySource;
import com.rabbitshop.kafkasample.commons.utils.UuidGenerationUtils;
import com.rabbitshop.kafkasample.producer.services.KafkaProducerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Random;


@Slf4j
@FieldDefaults(level = AccessLevel.PROTECTED)
@Getter(value = AccessLevel.PROTECTED)
public abstract class AbstractKafkaProducerRunnableTask implements Runnable {

	static final String PROD_ID = "PROD-00001A";

	static final String SOURCE_ID = "SOURCE-001";

	static final String EMPLOYEE_ID = "EMPL-ID-001";

	long totalSentMessages = 0;

	@Resource(name = "kafkaProducerService")
	KafkaProducerService kafkaProducerService;

	public abstract long getRate();

	@Override
	public abstract void run();

	public long getTotalSentMessages() {

		return totalSentMessages;
	}

	protected void sendInventory(final InventoryOperation operation, final long qty) {

		log.info("Sending inventory {} with qty {}...", operation.getValue(), qty);

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
