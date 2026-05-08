package com.iacodereview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@ConfigurationPropertiesScan
public class IaCodeReviewApplication {
    static void main(String[] args) {
        SpringApplication.run(IaCodeReviewApplication.class, args);
    }

    @Bean
    public RestClient.Builder builder() {
        return RestClient.builder();
    }
}
