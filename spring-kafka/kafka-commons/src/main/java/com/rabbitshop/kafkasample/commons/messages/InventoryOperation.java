package com.rabbitshop.kafkasample.commons.messages;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter(value = AccessLevel.PUBLIC)
public enum InventoryOperation implements Serializable {

	ADD("ADD"),
	SUB("SUB"),
	DELETE_PROD_INV("DELETE_PROD_INV");

	String value;

	InventoryOperation(final String value) {

		this.value = value;
	}

}
