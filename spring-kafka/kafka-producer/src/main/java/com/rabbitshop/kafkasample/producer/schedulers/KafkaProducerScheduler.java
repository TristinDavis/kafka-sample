package com.rabbitshop.kafkasample.producer.schedulers;

public interface KafkaProducerScheduler {

	void sendInventoryAddMessage();

	void sendInventorySubMessage();

	void sendInventoryDeleteMessage();

}
