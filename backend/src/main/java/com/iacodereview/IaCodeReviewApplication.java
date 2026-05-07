package com.iacodereview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class IaCodeReviewApplication {
    public static void main(String[] args) {
        SpringApplication.run(IaCodeReviewApplication.class, args);
    }
}
