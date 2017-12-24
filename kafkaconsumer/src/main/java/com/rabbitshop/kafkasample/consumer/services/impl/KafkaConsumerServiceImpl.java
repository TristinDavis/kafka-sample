package com.rabbitshop.kafkasample.consumer.services.impl;

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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service("kafkaConsumerService")
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

	@Resource(name = "kafkaConsumerConfig")
	@Getter(value = AccessLevel.PROTECTED)
	@Setter
	KafkaConsumerConfig kafkaConsumerConfig;

	@Getter(value = AccessLevel.PRIVATE)
	List<SimpleMsg> simpleMessages;

	@Getter(value = AccessLevel.PRIVATE)
	List<ComplexMsg> complexMessages;

	@Getter(value = AccessLevel.PRIVATE)
	List<NestedMsg> nestedMessages;

	@Getter(value = AccessLevel.PROTECTED)
	CountDownLatch countDownLatch;

	@PostConstruct
	protected void postConstruct() {

		log.debug("Post constructing...");

		simpleMessages = new ArrayList<>();
		complexMessages = new ArrayList<>();
		nestedMessages = new ArrayList<>();
		countDownLatch = new CountDownLatch(
				getKafkaConsumerConfig().getCountDownLatchSeconds());
	}

	@Override
	@KafkaListener(topics = "${kafka.topic.simple.name}", groupId = "${kafka.group.id}",
			containerFactory = "simpleKafkaListenerContainerFactory", errorHandler = "kafkaListenerErrorHandler")
	public void listenSimpleGroup(final String message) {

		log.info("Received new simple message in topic {} of group {}: {}",
				getKafkaConsumerConfig().getSimpleTopicName(), getKafkaConsumerConfig().getGroupId(), message);

		getSimpleMessages().add(
				SimpleMsg.builder()
						.message(message)
						.build());

		getCountDownLatch().countDown();
	}

	@Override
	@KafkaListener(topics = "${kafka.topic.complex.name}", groupId = "${kafka.group.id}",
			containerFactory = "complexKafkaListenerContainerFactory", errorHandler = "kafkaListenerErrorHandler")
	public void listenComplexGroup(final ComplexMsg multiFieldsMsg) {

		log.info("Received complex message number {} in topic {} of group {}: Author[{}] Message[{}]",
				multiFieldsMsg.getNumber(), getKafkaConsumerConfig().getComplexTopicName(), getKafkaConsumerConfig().getGroupId(),
				multiFieldsMsg.getAuthor(), multiFieldsMsg.getMessage());

		getComplexMessages().add(multiFieldsMsg);

		getCountDownLatch().countDown();
	}

	@Override
	@KafkaListener(topics = "${kafka.topic.nested.name}", groupId = "${kafka.group.id}",
			containerFactory = "nestedKafkaListenerContainerFactory", errorHandler = "kafkaListenerErrorHandler")
	public void listenNestedGroup(final NestedMsg nestedMsg) {

		log.info("{} - Received nested message number {} in topic {} of group {}: Author[{} {}] Message[{}]",
				nestedMsg.getDateTimeReceived(), nestedMsg.getNumber(),
				getKafkaConsumerConfig().getNestedTopicName(), getKafkaConsumerConfig().getGroupId(),
				nestedMsg.getAuthor().getName(), nestedMsg.getAuthor().getSurname(), nestedMsg.getMessage());

		getNestedMessages().add(nestedMsg);

		getCountDownLatch().countDown();
	}

	@Override
	public List<SimpleMsg> listSimpleMessages() {

		log.debug("List all simple messages received on topic {} ...",
				getKafkaConsumerConfig().getSimpleTopicName());

		return getSimpleMessages();
	}

	@Override
	public List<ComplexMsg> listComplexMessages() {

		log.debug("List all complex messages received on topic {} ...",
				getKafkaConsumerConfig().getComplexTopicName());

		return getComplexMessages();
	}

	@Override
	public List<NestedMsg> listNestedMessages() {

		log.debug("List all nested messages received on topic {} ...",
				getKafkaConsumerConfig().getNestedTopicName());

		return getNestedMessages();
	}

}
