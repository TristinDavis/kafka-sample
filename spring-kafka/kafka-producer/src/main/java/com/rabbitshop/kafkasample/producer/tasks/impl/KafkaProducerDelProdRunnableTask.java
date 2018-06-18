package com.rabbitshop.kafkasample.producer.tasks.impl;

import com.rabbitshop.kafkasample.commons.messages.InventoryOperation;
import com.rabbitshop.kafkasample.producer.tasks.AbstractKafkaProducerRunnableTask;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component("kafkaProducerDelProdRunnableTask")
public class KafkaProducerDelProdRunnableTask extends AbstractKafkaProducerRunnableTask {

	static long DEL_RATE = 60000L;

	@Override
	public long getRate() {

		return DEL_RATE;
	}

	@Override
	public void run() {

		log.debug("Sending inventory DELETE msg...");

		sendInventory(InventoryOperation.DELETE_PROD_INV, 0L);
		totalSentMessages++;
	}

}
