package com.app.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {
	
	@Bean
	WebClient productWebClient(
	        @Value("${product.service.url:http://localhost:8081}") String productServiceUrl) {
	    return WebClient.builder()
	            .baseUrl(productServiceUrl)
	            .build();
	}
}
