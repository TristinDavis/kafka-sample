package com.rabbitshop.kafkasample.consumer.controllers;

import com.rabbitshop.kafkasample.commons.messages.ComplexMsg;
import com.rabbitshop.kafkasample.commons.messages.NestedMsg;
import com.rabbitshop.kafkasample.commons.messages.SimpleMsg;
import com.rabbitshop.kafkasample.consumer.config.KafkaConsumerConfig;
import com.rabbitshop.kafkasample.consumer.services.KafkaConsumerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/kafka/consumer")
class KafkaConsumerController {

	@Resource(name = "kafkaConsumerConfig")
	@Getter(value = AccessLevel.PROTECTED)
	@Setter
	KafkaConsumerConfig kafkaConsumerConfig;

	@Resource(name = "kafkaConsumerService")
	@Getter(value = AccessLevel.PROTECTED)
	@Setter
	KafkaConsumerService kafkaConsumerService;

	@GetMapping("/simple")
	public List<SimpleMsg> listSimpleMessages() {

		log.info("Listing all simple messages received on topic {} of group {}...",
				getKafkaConsumerConfig().getSimpleTopicName(), getKafkaConsumerConfig().getGroupId());

		final List<SimpleMsg> messages = getKafkaConsumerService().listSimpleMessages();
		log.debug("Topic[{}] Group[{}] - Messages:\n{}",
				getKafkaConsumerConfig().getSimpleTopicName(), getKafkaConsumerConfig().getGroupId(),
				String.join("\n", messages.toString()));

		return messages;
	}

	@GetMapping("/complex")
	public List<ComplexMsg> listComplexMessages() {

		log.info("Listing all complex messages received on topic {} of group {}...",
				getKafkaConsumerConfig().getComplexTopicName(), getKafkaConsumerConfig().getGroupId());

		final List<ComplexMsg> messages = getKafkaConsumerService().listComplexMessages();
		log.debug("Topic[{}] Group[{}] - Messages:\n{}",
				getKafkaConsumerConfig().getComplexTopicName(), getKafkaConsumerConfig().getGroupId(),
				String.join("\n", messages.toString()));

		return messages;
	}

	@GetMapping("/nested")
	public List<NestedMsg> listNestedMessages() {

		log.info("Listing all nested messages received on topic {} of group {}...",
				getKafkaConsumerConfig().getNestedTopicName(), getKafkaConsumerConfig().getGroupId());

		final List<NestedMsg> messages = getKafkaConsumerService().listNestedMessages();
		log.debug("Topic[{}] Group[{}] - Messages:\n{}",
				getKafkaConsumerConfig().getNestedTopicName(), getKafkaConsumerConfig().getGroupId(),
				String.join("\n", messages.toString()));

		return messages;
	}

}
