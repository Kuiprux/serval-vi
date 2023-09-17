package com.ndc.servalvi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ndc.servalvi", "com.ndc.servalvi.controller"} )
public class ServalViApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServalViApplication.class, args);
	}

}
