package com.rabbitshop.kafkasample.consumer.controllers;

import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import com.rabbitshop.kafkasample.consumer.config.KafkaConsumerConfig;
import com.rabbitshop.kafkasample.consumer.services.KafkaConsumerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PROTECTED)
@RestController
@RequestMapping("/kafka/consumer")
class KafkaConsumerController {

	static final String NEW_LINE = "\n";

	@Resource(name = "kafkaConsumerConfig")
	KafkaConsumerConfig kafkaConsumerConfig;

	@Resource(name = "kafkaConsumerService")
	KafkaConsumerService kafkaConsumerService;

	@GetMapping("/inventory")
	public List<InventoryMsg> listInventoryMessages() {

		log.info("Listing all inventory messages received on topic {} of group {}...",
				getKafkaConsumerConfig().getTopicId(), getKafkaConsumerConfig().getGroupId());

		final List<InventoryMsg> messages = getKafkaConsumerService().listInventoryMessages();

		log.debug("Topic[{}] Group[{}] - Messages:{}{}", NEW_LINE,
				getKafkaConsumerConfig().getTopicId(), getKafkaConsumerConfig().getGroupId(),
				String.join(NEW_LINE, messages.toString()));

		return messages;
	}

}
