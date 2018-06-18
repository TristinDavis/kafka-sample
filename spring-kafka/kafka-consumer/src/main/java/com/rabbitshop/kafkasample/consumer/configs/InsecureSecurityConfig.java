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
@Profile(KafkaConsumerConstants.SPRING_PROFILE_INSECURE)
public class InsecureSecurityConfig extends WebSecurityConfigurerAdapter {

	/*
	 * PLEASE NOTE: For sake of simplicity we are omitting specific security configurations
	 */
	@Override
	protected void configure(final HttpSecurity http) throws Exception {

		log.debug("Loading INSECURE security config...");

		http
				.authorizeRequests()
				.anyRequest().permitAll()

				.and()
				.httpBasic()

				.and()
				.cors().disable()
				.csrf().disable();
	}

}
