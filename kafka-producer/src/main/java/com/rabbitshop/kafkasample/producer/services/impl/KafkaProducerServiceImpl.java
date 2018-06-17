package com.rabbitshop.kafkasample.producer.services.impl;

import com.rabbitshop.kafkasample.commons.messages.InventoryMsg;
import com.rabbitshop.kafkasample.commons.messages.NestedMsg;
import com.rabbitshop.kafkasample.producer.config.KafkaProducerConfig;
import com.rabbitshop.kafkasample.producer.services.KafkaProducerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PROTECTED)
@Service("kafkaProducerService")
public class KafkaProducerServiceImpl implements KafkaProducerService {

	@Resource(name = "kafkaProducerConfig")
	KafkaProducerConfig kafkaProducerConfig;

	@Resource(name = "inventoryKafkaTemplate")
	KafkaTemplate<String, InventoryMsg> inventoryKafkaTemplate;

	@Resource(name = "nestedKafkaTemplate")
	KafkaTemplate<String, NestedMsg> nestedKafkaTemplate;

	@Override
	public void sendInventoryMessage(final InventoryMsg inventoryMsg) {

		final String topicName = getKafkaProducerConfig().getTopicId();

		log.info("Publishing inventory message in topic {}: id {}, prodId {}, qty {}, op {} date {}",
				inventoryMsg.getDateTime(), getKafkaProducerConfig().getTopicId(),
				inventoryMsg.getId(), inventoryMsg.getProductId(), inventoryMsg.getQuantity(), inventoryMsg.getOperation(), inventoryMsg.getDateTime());

		getInventoryKafkaTemplate().send(topicName, inventoryMsg);
	}

}
