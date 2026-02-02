package com.ahmedyousef.backend_assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BackendAssessmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendAssessmentApplication.class, args);
    }

}
