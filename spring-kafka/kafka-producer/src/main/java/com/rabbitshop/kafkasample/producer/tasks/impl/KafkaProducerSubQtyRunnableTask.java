package com.rabbitshop.kafkasample.producer.tasks.impl;

import com.rabbitshop.kafkasample.commons.messages.InventoryOperation;
import com.rabbitshop.kafkasample.producer.tasks.AbstractKafkaProducerRunnableTask;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component("kafkaProducerSubQtyRunnableTask")
public class KafkaProducerSubQtyRunnableTask extends AbstractKafkaProducerRunnableTask {

	static long SUB_RATE = 5000L;

	@Override
	public long getRate() {

		return SUB_RATE;
	}

	@Override
	public void run() {

		log.debug("Sending inventory SUB msg...");

		sendInventory(InventoryOperation.SUB, getRandomQty());
		totalSentMessages++;
	}

}
