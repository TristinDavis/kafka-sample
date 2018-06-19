package com.rabbitshop.kafkasample.commons.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.annotation.Resource;


@Configuration(value = "kafkaCommonsJacksonConfig")
@Order(1)
class JacksonConfig {

	@Bean("jsonObjectMapper")
	@Resource
	public ObjectMapper createJacksonObjectMapper(final Jackson2ObjectMapperBuilder builder) {

		final ObjectMapper objectMapper =
				builder.createXmlMapper(false).build()
						.registerModule(new ParameterNamesModule())
						.registerModule(new Jdk8Module())
						.registerModule(new JavaTimeModule()); // new module, NOT JSR310Module

		// objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		// objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

		return objectMapper;
	}

}
