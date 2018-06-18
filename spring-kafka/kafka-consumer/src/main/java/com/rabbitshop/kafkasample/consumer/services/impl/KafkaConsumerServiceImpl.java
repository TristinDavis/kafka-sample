package com.rabbitshop.kafkasample.consumer.services.impl;

import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import com.rabbitshop.kafkasample.consumer.configs.KafkaConsumerConfig;
import com.rabbitshop.kafkasample.consumer.services.KafkaConsumerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PROTECTED)
@Service("kafkaConsumerService")
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

	@Resource(name = "kafkaConsumerConfig")
	KafkaConsumerConfig kafkaConsumerConfig;

	List<InventoryMsg> inventoryMessages;

	CountDownLatch countDownLatch;

	@PostConstruct
	protected void postConstruct() {

		log.debug("Post constructing...");

		inventoryMessages = new ArrayList<>();

		countDownLatch = new CountDownLatch(
				getKafkaConsumerConfig().getCountDownLatchSeconds());
	}

	@Override
	@KafkaListener(topics = "${kafka.topic.id}", groupId = "${kafka.group.id}",
			containerFactory = "inventoryKafkaListenerContainerFactory", errorHandler = "kafkaListenerErrorHandler")
	public void listenInventoryGroup(final InventoryMsg inventoryMsg) {

		log.info("Received inventory message in topic {} of group {}: id {}, prodId {}, qty {}, op {}, date {}",
				getKafkaConsumerConfig().getTopicId(), getKafkaConsumerConfig().getGroupId(),
				inventoryMsg.getId(), inventoryMsg.getProductId(), inventoryMsg.getQuantity(), inventoryMsg.getOperation(), inventoryMsg.getDateTime());

		getInventoryMessages().add(inventoryMsg);

		getCountDownLatch().countDown();
	}

	@Override
	public List<InventoryMsg> listInventoryMessages() {

		log.debug("List all inventory messages received on topic {} ...",
				getKafkaConsumerConfig().getTopicId());

		return getInventoryMessages();
	}

}
