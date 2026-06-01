package com.topleague.predict.infrastructure.out.rest.game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface GameFeignClient {
    
    @GetMapping("/games/{id}")
    WebGameResponse getGameById(@PathVariable("id") Integer id);
}
