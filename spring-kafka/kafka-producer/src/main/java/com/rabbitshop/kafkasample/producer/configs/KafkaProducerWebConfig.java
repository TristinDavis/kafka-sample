package com.rabbitshop.kafkasample.producer.configs;

import com.rabbitshop.kafkasample.producer.converters.KafkaProducerOperationConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Configuration(value = "kafkaProducerWebConfig")
@Order(3)
public class KafkaProducerWebConfig extends WebMvcConfigurationSupport {

	@Resource(name = "kafkaProducerOperationConverter")
	KafkaProducerOperationConverter kafkaProducerOperationConverter;

	@Override
	public FormattingConversionService mvcConversionService() {

		log.debug("Registering custom converters...");

		final FormattingConversionService formattingConversionService = super.mvcConversionService();
		formattingConversionService.addConverter(getKafkaProducerOperationConverter());
		return formattingConversionService;
	}

}
