package com.rabbitshop.kafkasample.producer.controllers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter(value = AccessLevel.PUBLIC)
public enum KafkaProducerOperation {

	ADD("ADD"),
	SUB("SUB"),
	DEL("DEL");

	String value;

	KafkaProducerOperation(final String value) {

		this.value = value;
	}

}
