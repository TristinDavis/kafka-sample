package com.rabbitshop.kafkasample.producer.converters;

import com.rabbitshop.kafkasample.producer.controllers.KafkaProducerOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


@Slf4j
@Component("kafkaProducerOperationConverter")
public class KafkaProducerOperationConverter implements Converter<String, KafkaProducerOperation> {

	@Override
	public KafkaProducerOperation convert(final String source) {

		try {
			return KafkaProducerOperation.valueOf(source);
		} catch (final Exception e) {
			return null; // or return a default
		}
	}

}