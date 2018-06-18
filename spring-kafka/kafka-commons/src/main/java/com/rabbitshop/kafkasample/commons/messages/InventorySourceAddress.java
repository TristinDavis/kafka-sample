package com.rabbitshop.kafkasample.commons.messages;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


// TODO setup non-null annotations
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Data
@Builder
public class InventorySourceAddress implements Serializable {

	String line1;

	String line2;

	String zipcode;

	String city;

	String canton;

	String country;

}
