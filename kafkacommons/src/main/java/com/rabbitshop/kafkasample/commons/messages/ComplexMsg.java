package com.rabbitshop.kafkasample.commons.messages;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ComplexMsg {

	String message;

	String author;

	int number;

}
