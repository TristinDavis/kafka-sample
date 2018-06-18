package com.rabbitshop.kafkasample.consumer.configs;

import com.rabbitshop.kafkasample.consumer.constants.KafkaConsumerConstants;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Configuration
@Order(100)
@EnableWebSecurity
@Profile(KafkaConsumerConstants.SPRING_PROFILE_SECURE)
public class SecureSecurityConfig extends WebSecurityConfigurerAdapter {

	static String KAFKA_CONSUMER_URL_MATCHER = "/kafka/consumer/**";

	/*
	 * PLEASE NOTE: For sake of simplicity we are omitting specific security configurations
	 */
	@Override
	protected void configure(final HttpSecurity http) throws Exception {

		log.debug("Loading SECURE security config...");

		http
				.authorizeRequests()
				.mvcMatchers(KAFKA_CONSUMER_URL_MATCHER).permitAll()
				.anyRequest().authenticated()

				.and()
				.httpBasic()

				.and()
				.cors().disable()
				.csrf().disable();
	}

}
