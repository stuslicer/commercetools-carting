package com.kingfisher.commerceclouddumbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CommerceCloudDumboApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommerceCloudDumboApplication.class, args);
	}

}
