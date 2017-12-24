package com.rabbitshop.kafkasample.consumer.services;

import com.rabbitshop.kafkasample.commons.messages.ComplexMsg;
import com.rabbitshop.kafkasample.commons.messages.NestedMsg;
import com.rabbitshop.kafkasample.commons.messages.SimpleMsg;

import java.util.List;


public interface KafkaConsumerService {

	void listenSimpleGroup(final String message);

	void listenComplexGroup(final ComplexMsg complexMsg);

	void listenNestedGroup(final NestedMsg nestedMsg);

	List<SimpleMsg> listSimpleMessages();

	List<ComplexMsg> listComplexMessages();

	List<NestedMsg> listNestedMessages();

}
