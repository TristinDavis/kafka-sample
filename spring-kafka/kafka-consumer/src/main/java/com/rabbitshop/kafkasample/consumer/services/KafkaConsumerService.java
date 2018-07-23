package com.rabbitshop.kafkasample.consumer.services;

import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import org.springframework.messaging.MessageHeaders;

import java.util.List;


public interface KafkaConsumerService {

	void listenInventoryGroup(final InventoryMsg inventoryMsg, final MessageHeaders headers);

	List<InventoryMsg> listInventoryMessages();

}
