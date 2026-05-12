package com.project.golfofficeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GolfOfficeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GolfOfficeApiApplication.class, args);
    }

}
