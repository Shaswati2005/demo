package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OllamaConfig {

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {

        return restClientBuilder
                .baseUrl(baseUrl)
                .build();

    }

}