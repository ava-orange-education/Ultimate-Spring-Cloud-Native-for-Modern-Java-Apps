package com.campusflow;

import com.campusflow.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class CampusFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusFlowApplication.class, args);
    }
}
