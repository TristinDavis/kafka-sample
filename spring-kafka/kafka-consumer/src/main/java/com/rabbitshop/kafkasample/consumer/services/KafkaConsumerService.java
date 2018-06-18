package com.rabbitshop.kafkasample.consumer.services;

import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;

import java.util.List;


public interface KafkaConsumerService {

	void listenInventoryGroup(final InventoryMsg inventoryMsg);

	List<InventoryMsg> listInventoryMessages();

}
