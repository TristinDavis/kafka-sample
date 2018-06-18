package com.rabbitshop.kafkasample.producer.configs;

import com.rabbitshop.kafkasample.producer.constants.KafkaProducerConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * PLEASE NOTE: This configuration class is just to enable the scheduling execution just when the profile "schedule" is active
 */
@Configuration
@Order(10)
@Profile(KafkaProducerConstants.SPRING_PROFILE_SCHEDULE)
@EnableScheduling
public class KafkaProducerSchedulingConfig {

	// no-op
}
