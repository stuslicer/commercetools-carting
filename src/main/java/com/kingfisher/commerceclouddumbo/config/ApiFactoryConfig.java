package com.kingfisher.commerceclouddumbo.config;

import com.commercetools.api.client.ProjectApiRoot;
import com.kingfisher.commerceclouddumbo.api.ClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiFactoryConfig {

    @Bean
    public ProjectApiRoot apiClient(ClientFactory clientFactory) {
        return clientFactory.createApiClient();
    }
}
