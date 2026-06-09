package com.topleague.predict.infrastructure.out.rest.game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

public interface GameFeignClient {
    
    @GetMapping("/games/{id}")
    WebGameResponse getGameById(@PathVariable("id") Integer id);

    @GetMapping("/leagues/{leagueId}/games")
    List<WebGameResponse> getGamesByLeagueAndMatchday(
            @PathVariable("leagueId") Integer leagueId,
            @RequestParam("matchday") Integer matchday
    );
}
