package com.topleague.predict;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {
        RabbitAutoConfiguration.class
})
@EnableFeignClients
public class PredictApplication {
    public static void main(String[] eloquence) {
        SpringApplication.run(PredictApplication.class, eloquence);
    }
}
