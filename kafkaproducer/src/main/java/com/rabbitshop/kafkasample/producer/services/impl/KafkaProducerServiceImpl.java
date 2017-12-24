package com.rabbitshop.kafkasample.producer.services.impl;

import com.rabbitshop.kafkasample.commons.messages.ComplexMsg;
import com.rabbitshop.kafkasample.commons.messages.NestedMsg;
import com.rabbitshop.kafkasample.commons.messages.SimpleMsg;
import com.rabbitshop.kafkasample.producer.config.KafkaProducerConfig;
import com.rabbitshop.kafkasample.producer.services.KafkaProducerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service("kafkaProducerService")
public class KafkaProducerServiceImpl implements KafkaProducerService {

	@Resource(name = "kafkaProducerConfig")
	@Getter(value = AccessLevel.PROTECTED)
	@Setter
	KafkaProducerConfig kafkaProducerConfig;

	@Resource(name = "simpleKafkaTemplate")
	@Getter(value = AccessLevel.PROTECTED)
	@Setter
	KafkaTemplate<String, String> simpleKafkaTemplate;

	@Resource(name = "complexKafkaTemplate")
	@Getter(value = AccessLevel.PROTECTED)
	@Setter
	KafkaTemplate<String, ComplexMsg> complexKafkaTemplate;

	@Resource(name = "nestedKafkaTemplate")
	@Getter(value = AccessLevel.PROTECTED)
	@Setter
	KafkaTemplate<String, NestedMsg> nestedKafkaTemplate;

	@Getter
	@Setter(value = AccessLevel.PROTECTED)
	int counter = 0;

	@Override
	public void sendMessage(final SimpleMsg simpleMsg) {

		final String topicName = getTopicName(TopicType.SIMPLE);

		log.debug("Publishing message {} to topic {}", simpleMsg.getMessage(), topicName);

		getSimpleKafkaTemplate().send(topicName, simpleMsg.getMessage());
	}

	@Override
	public void sendMessage(final ComplexMsg complexMsg) {

		final String topicName = getTopicName(TopicType.COMPLEX);

		log.debug("Publishing message {} of author {} to topic {}",
				complexMsg.getMessage(), complexMsg.getAuthor(), topicName);

		complexMsg.setNumber(getNextCounter());

		getComplexKafkaTemplate().send(topicName, complexMsg);
	}

	@Override
	public void sendMessage(final NestedMsg nestedMsg) {

		final String topicName = getTopicName(TopicType.NESTED);

		log.debug("Publishing message {} of author {} {} to topic {}",
				nestedMsg.getMessage(), nestedMsg.getAuthor().getName(), nestedMsg.getAuthor().getSurname(), topicName);

		populateDynamicAttribute(nestedMsg);

		getNestedKafkaTemplate().send(topicName, nestedMsg);
	}

	protected void populateDynamicAttribute(final NestedMsg nestedMsg) {

		nestedMsg.setDateTimeReceived(LocalDateTime.now());
		nestedMsg.setNumber(getNextCounter());
	}

	protected String getTopicName(final TopicType type) {

		switch (type) {
			case SIMPLE:
				return getKafkaProducerConfig().getSimpleTopicName();
			case COMPLEX:
				return getKafkaProducerConfig().getComplexTopicName();
			case NESTED:
				return getKafkaProducerConfig().getNestedTopicName();
			default:
				return getKafkaProducerConfig().getNestedTopicName();
		}
	}

	protected int getNextCounter() {

		setCounter(getCounter() + 1);
		return getCounter();
	}

	enum TopicType {
		SIMPLE, COMPLEX, NESTED
	}

}
