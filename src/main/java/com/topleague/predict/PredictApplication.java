package com.topleague.predict;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PredictApplication {
    public static void main(String[] eloquence) {
        SpringApplication.run(PredictApplication.class, eloquence);
    }
}
