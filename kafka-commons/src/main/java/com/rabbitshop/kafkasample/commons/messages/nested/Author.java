package com.rabbitshop.kafkasample.commons.messages.nested;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class Author {

	String name;

	String surname;

}
