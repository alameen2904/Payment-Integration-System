package com.project.payments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import com.project.payments.security.ExceptionHandlerFilter;
import com.project.payments.security.HmacSha256Filter;
import com.project.payments.service.HmacSha256Service;
import com.project.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final HmacSha256Service hmacSha256Service;
	private final JsonUtil jsonUtil; // 💡 JSON Util dependency add pannittaen

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		.csrf(csrf -> csrf.disable())
		.httpBasic(Customizer.withDefaults())
		.authorizeHttpRequests(authorize -> authorize
				.anyRequest().authenticated()
				)
		.addFilterBefore(new ExceptionHandlerFilter( jsonUtil), 
				DisableEncodeUrlFilter.class)             		
		.addFilterAfter(new HmacSha256Filter(hmacSha256Service, jsonUtil), 
				LogoutFilter.class) // HmacSha256Filter-aiyum add pannanum machi
		.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				);

		return http.build();
	}
}