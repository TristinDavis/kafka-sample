package com.rabbitshop.kafkasample.producer.services.impl;

import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import com.rabbitshop.kafkasample.producer.configs.KafkaProducerConfig;
import com.rabbitshop.kafkasample.producer.services.KafkaProducerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PROTECTED)
@Service("kafkaProducerService")
public class KafkaProducerServiceImpl implements KafkaProducerService {

	@Resource(name = "kafkaProducerConfig")
	KafkaProducerConfig kafkaProducerConfig;

	@Autowired
	KafkaTemplate<String, InventoryMsg> kafkaTemplate;

	@Override
	public void sendInventoryMessage(final InventoryMsg inventoryMsg) {

		final String topicId = getTopicId();

		// Version 1 - Object with topic
		// log.info("Publishing inventory message in topic {}: id {}, prodId {}, qty {}, op {} date {}",
		// 		topicId,
		// 		inventoryMsg.getId(), inventoryMsg.getProductId(), inventoryMsg.getQuantity(), inventoryMsg.getOperation(), inventoryMsg.getDateTime());
		// getKafkaTemplate().send(topicId, inventoryMsg);

		// Version 2a - Message
		// log.info("Publishing inventory message in topic {}: id {}, prodId {}, qty {}, op {} date {}",
		// 		topicId,
		// 		inventoryMsg.getId(), inventoryMsg.getProductId(), inventoryMsg.getQuantity(), inventoryMsg.getOperation(), inventoryMsg.getDateTime());
		// final Message<InventoryMsg> message = MessageBuilder
		// 		.withPayload(inventoryMsg)
		// 		.build();
		// getKafkaTemplate().send(message);

		// Version 2b - Message with header
		log.info("Publishing inventory message in topic {}: id {}, prodId {}, qty {}, op {} date {}",
				topicId,
				inventoryMsg.getId(), inventoryMsg.getProductId(), inventoryMsg.getQuantity(), inventoryMsg.getOperation(), inventoryMsg.getDateTime());
		final Message<InventoryMsg> message = MessageBuilder
				// payload
				.withPayload(inventoryMsg)
				// headers
				.setHeaderIfAbsent(KafkaHeaders.TOPIC, topicId)
				.setHeaderIfAbsent(KafkaHeaders.CONSUMER, getClientId())
				.setHeaderIfAbsent(KafkaHeaders.TIMESTAMP, new Date().getTime())
				.setHeaderIfAbsent(KafkaHeaders.REPLY_TOPIC, "TEST")
				.build();
		getKafkaTemplate().send(message);
	}

	protected String getTopicId() {

		final String topic = getKafkaProducerConfig().getTopicId();

		if (StringUtils.isEmpty(topic)) {
			final String defaultTopic = getKafkaProducerConfig().getDefaultTopicId();
			log.debug("Using producer default topic: {}", defaultTopic);
			return defaultTopic;
		}

		log.debug("Using producer topic: {}", topic);
		return topic;
	}

	protected String getClientId() {

		return getKafkaProducerConfig().getClientId();
	}

}
