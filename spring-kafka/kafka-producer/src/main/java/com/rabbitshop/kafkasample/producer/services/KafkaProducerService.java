package com.rabbitshop.kafkasample.producer.services;


import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;


public interface KafkaProducerService {

	void sendInventoryMessage(final InventoryMsg inventoryMsg);

}
