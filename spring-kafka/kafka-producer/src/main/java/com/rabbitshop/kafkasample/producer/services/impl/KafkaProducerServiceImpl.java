package com.rabbitshop.kafkasample.producer.services.impl;

import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import com.rabbitshop.kafkasample.producer.configs.KafkaProducerConfig;
import com.rabbitshop.kafkasample.producer.services.KafkaProducerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PROTECTED)
@Service("kafkaProducerService")
public class KafkaProducerServiceImpl implements KafkaProducerService {

	@Resource(name = "kafkaProducerConfig")
	KafkaProducerConfig kafkaProducerConfig;

	// Version 1 - Tracing not working properly
	@Resource(name = "inventoryKafkaTemplate")
	KafkaTemplate<String, InventoryMsg> inventoryKafkaTemplate;

	@Override
	public void sendInventoryMessage(final InventoryMsg inventoryMsg) {

		final String topicId = getTopicId();

		log.info("Publishing inventory message in topic {}: id {}, prodId {}, qty {}, op {} date {}",
				topicId,
				inventoryMsg.getId(), inventoryMsg.getProductId(), inventoryMsg.getQuantity(), inventoryMsg.getOperation(), inventoryMsg.getDateTime());

		getInventoryKafkaTemplate().send(topicId, inventoryMsg);
	}

	// Version 2/3
	// @Autowired
	// KafkaTemplate<String, InventoryMsg> kafkaTemplate;
	//
	// @Override
	// public void sendInventoryMessage(final InventoryMsg inventoryMsg) {

	// Version 2 - Object with topic
	// 	final String topicId = getTopicId();
	//
	// 	log.info("Publishing inventory message in topic {}: id {}, prodId {}, qty {}, op {} date {}",
	// 			topicId,
	// 			inventoryMsg.getId(), inventoryMsg.getProductId(), inventoryMsg.getQuantity(), inventoryMsg.getOperation(), inventoryMsg.getDateTime());
	//
	// 	getKafkaTemplate().send(topicId, inventoryMsg);

	// Version 3a - Message with topic
	// log.info("Publishing inventory message in topic XXX: id {}, prodId {}, qty {}, op {} date {}",
	// 		inventoryMsg.getId(), inventoryMsg.getProductId(), inventoryMsg.getQuantity(), inventoryMsg.getOperation(), inventoryMsg.getDateTime());
	// final Message<InventoryMsg> message = MessageBuilder
	// 		.withPayload(inventoryMsg)
	// 		.setHeader(KafkaHeaders.TOPIC, getTopicId())
	// 		.build();
	//
	// getKafkaTemplate().send(message);

	// Version 3b - Message with default topic
	// log.info("Publishing inventory message in topic XXX: id {}, prodId {}, qty {}, op {} date {}",
	// 		inventoryMsg.getId(), inventoryMsg.getProductId(), inventoryMsg.getQuantity(), inventoryMsg.getOperation(), inventoryMsg.getDateTime());
	//
	// final Message<InventoryMsg> message = MessageBuilder
	// 		.withPayload(inventoryMsg)
	// 		.build();
	//
	// getKafkaTemplate().send(message);
	// }

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

}
