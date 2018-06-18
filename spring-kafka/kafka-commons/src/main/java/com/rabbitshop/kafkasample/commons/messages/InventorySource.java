package com.rabbitshop.kafkasample.commons.messages;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


// TODO setup non-null annotations
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Data // TODO replace with Getter for immutability?
@Builder
public class InventorySource implements Serializable {

	String id;

	String employeeId;

	InventorySourceAddress address;

}
