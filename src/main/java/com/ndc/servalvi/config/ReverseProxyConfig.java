package com.ndc.servalvi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
/*
@Configuration
public class ReverseProxyConfig {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@RestController
	public static class ReverseProxyController {

		@Autowired
		private RestTemplate restTemplate;

		@RequestMapping("/socket.io")
		public String forwardApp1() {
			String response = restTemplate.getForObject("http://localhost:3000/socket.io", String.class);
			return "Forwarded to app1: " + response;
		}
	}
}*/