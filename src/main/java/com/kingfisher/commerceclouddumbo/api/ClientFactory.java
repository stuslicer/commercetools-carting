package com.kingfisher.commerceclouddumbo.api;// Add your package information here

// Required imports

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import io.vrap.rmf.base.client.oauth2.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

//@Component
@Configuration

@ConfigurationProperties(prefix = "commercetools")
public class ClientFactory {

    @Value("${commercetools.project-key}")
    private String projectKey;
    @Value("${commercetools.client-id}")
    private String clientId;
    @Value("${commercetools.secret}")
    private String secret;
    @Value("${commercetools.scope}")
    private String scope;

    public ProjectApiRoot createApiClient() {
        // there are various methods here to handle the creation
        final ProjectApiRoot apiRoot = ApiRootBuilder.of()
                .defaultClient(ClientCredentials.of()
                                .withClientId(clientId)
                                .withClientSecret(secret)
                                .build(),
                        ServiceRegion.GCP_EUROPE_WEST1 )
                .build(projectKey);

        return apiRoot;
    }
}
