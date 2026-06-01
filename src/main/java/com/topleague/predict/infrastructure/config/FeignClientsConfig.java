package com.topleague.predict.infrastructure.config;

import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.topleague.predict.infrastructure.out.rest.game.GameFeignClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignClientsConfig {

    @Bean
    public GameFeignClient gameFeignClient(
            Decoder decoder,
            Encoder encoder,
            Contract contract,
            @Value("${topleague.client.game.url:http://localhost:8080/api}") String gameUrl,
            @Value("${topleague.client.game.connect-timeout-ms:3000}") long connectTimeoutMs,
            @Value("${topleague.client.game.read-timeout-ms:3000}") long readTimeoutMs) {

        return Feign.builder()
                .contract(contract)
                .encoder(encoder)
                .decoder(decoder)
                .options(new Request.Options(
                        connectTimeoutMs, TimeUnit.MILLISECONDS,
                        readTimeoutMs, TimeUnit.MILLISECONDS,
                        true))
                .logLevel(Logger.Level.BASIC)
                .target(GameFeignClient.class, gameUrl);
    }
}
