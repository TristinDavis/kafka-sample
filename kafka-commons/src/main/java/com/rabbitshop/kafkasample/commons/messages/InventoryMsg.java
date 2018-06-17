package com.rabbitshop.kafkasample.commons.messages;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


// TODO setup non-null annotations
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Data // TODO replace with Getter for immutability?
@Builder
public class InventoryMsg implements Serializable {

	String id;

	String productId;

	long quantity;

	InventoryOperation operation;

	InventorySource source;

	LocalDateTime dateTime;

	List<String> tags;

}
