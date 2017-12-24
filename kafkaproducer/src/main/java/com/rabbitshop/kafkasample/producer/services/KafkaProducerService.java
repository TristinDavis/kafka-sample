package com.rabbitshop.kafkasample.producer.services;


import com.rabbitshop.kafkasample.commons.messages.ComplexMsg;
import com.rabbitshop.kafkasample.commons.messages.NestedMsg;
import com.rabbitshop.kafkasample.commons.messages.SimpleMsg;


public interface KafkaProducerService {

	void sendMessage(final SimpleMsg simpleMsg);

	void sendMessage(final ComplexMsg complexMsg);

	void sendMessage(final NestedMsg nestedMsg);

}
