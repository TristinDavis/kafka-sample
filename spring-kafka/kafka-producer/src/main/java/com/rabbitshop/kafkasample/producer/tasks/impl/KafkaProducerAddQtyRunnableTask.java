package com.rabbitshop.kafkasample.producer.tasks.impl;

import com.rabbitshop.kafkasample.commons.messages.InventoryOperation;
import com.rabbitshop.kafkasample.producer.tasks.AbstractKafkaProducerRunnableTask;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component("kafkaProducerAddQtyRunnableTask")
public class KafkaProducerAddQtyRunnableTask extends AbstractKafkaProducerRunnableTask {

	static long ADD_RATE = 10000L;

	@Override
	public long getRate() {

		return ADD_RATE;
	}

	@Override
	public void run() {

		log.debug("Sending inventory ADD msg...");

		sendInventory(InventoryOperation.ADD, getRandomQty());
		totalSentMessages++;
	}

}
