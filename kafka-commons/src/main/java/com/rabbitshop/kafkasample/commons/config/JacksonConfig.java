package com.rabbitshop.kafkasample.commons.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.annotation.Resource;


@Configuration(value = "jacksonConfig")
@Order(1)
class JacksonConfig {

	@Bean("jsonObjectMapper")
	@Resource
	public ObjectMapper createJacksonObjectMapper(final Jackson2ObjectMapperBuilder builder) {

		final ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		// objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
		return objectMapper;
	}

}
