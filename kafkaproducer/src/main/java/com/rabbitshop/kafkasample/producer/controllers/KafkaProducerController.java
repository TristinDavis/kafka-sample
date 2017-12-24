package com.rabbitshop.kafkasample.producer.controllers;

import com.rabbitshop.kafkasample.commons.messages.ComplexMsg;
import com.rabbitshop.kafkasample.commons.messages.NestedMsg;
import com.rabbitshop.kafkasample.commons.messages.SimpleMsg;
import com.rabbitshop.kafkasample.commons.messages.nested.Author;
import com.rabbitshop.kafkasample.producer.services.KafkaProducerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/kafka/producer")
class KafkaProducerController {

	@Resource(name = "kafkaProducerService")
	@Getter(value = AccessLevel.PROTECTED)
	@Setter
	KafkaProducerService kafkaProducerService;

	@PostMapping("/simple")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void sendSimpleMsg(@RequestBody final SimpleMsg simpleMsg) {

		if (simpleMsg != null) {

			final String message = simpleMsg.getMessage();
			if (message != null
					&& !message.isEmpty()) {
				log.info("Sending message {} to Apache Kafka server...", message);

				getKafkaProducerService().sendMessage(simpleMsg);

			} else {
				log.error("Message is NULL or EMPTY, not sending...");
			}

		} else {
			log.error("MessageObject is NULL or EMPTY, not sending...");
		}
	}

	@PostMapping("/complex")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void sendComplexMsg(@RequestBody final ComplexMsg complexMsg) {

		if (complexMsg != null) {

			if (validateComplexMsg(complexMsg)) {
				log.info("Sending message {} from author {} to Apache Kafka server...",
						complexMsg.getMessage(), complexMsg.getAuthor());

				getKafkaProducerService().sendMessage(complexMsg);

			} else {
				log.error("Message or Author is NULL or EMPTY, not sending...");
			}

		} else {
			log.error("MessageObject is NULL or EMPTY, not sending...");
		}
	}

	@PostMapping("/nested")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void sendNestedMsg(@RequestBody final NestedMsg nestedMsg) {

		if (nestedMsg != null) {

			if (validateNestedMsg(nestedMsg)) {
				log.info("Sending message {} from author {} {} to Apache Kafka server...",
						nestedMsg.getMessage(), nestedMsg.getAuthor().getName(), nestedMsg.getAuthor().getSurname());

				getKafkaProducerService().sendMessage(nestedMsg);

			} else {
				log.error("Message or Author is NULL or EMPTY, not sending...");
			}

		} else {
			log.error("MessageObject is NULL or EMPTY, not sending...");
		}
	}

	protected boolean validateComplexMsg(final ComplexMsg complexMsg) {

		final String message = complexMsg.getMessage();
		final String author = complexMsg.getAuthor();

		if (message != null && !message.isEmpty()
				&& author != null && !author.isEmpty()) {
			return true;
		}
		return false;
	}

	protected boolean validateNestedMsg(final NestedMsg nestedMsg) {

		final String message = nestedMsg.getMessage();
		final Author author = nestedMsg.getAuthor();

		if (message != null && !message.isEmpty()
				&& author != null
				&& author.getName() != null && !author.getName().isEmpty()
				&& author.getSurname() != null && !author.getSurname().isEmpty()) {
			return true;
		}
		return false;
	}

}