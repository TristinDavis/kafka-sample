package com.rabbitshop.kafkasample.commons.messages;

import com.rabbitshop.kafkasample.commons.messages.nested.Author;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class NestedMsg {

	int number;

	String message;

	Author author;

	LocalDateTime dateTimeReceived;

	List<String> tags;

}
