package com.rabbitshop.kafkasample.consumer.errors.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Slf4j
@Component("kafkaListenerErrorHandler")
public class KafkaListenerErrorHandlerImpl implements KafkaListenerErrorHandler {

	@Override
	public Object handleError(final Message<?> message, final ListenerExecutionFailedException exception) throws Exception {

		log.error("Error receiving message: {} ", message.getPayload());
		log.error("Exception message: {}", exception.getMessage());

		return "KAFKA-ERROR-MSG";
	}

}
