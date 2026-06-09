package com.topleague.predict.infrastructure.out.rest.game;

import com.topleague.predict.application.port.out.game.GameGetByIdRepository;
import com.topleague.predict.application.port.out.game.GameGetByLeagueAndMatchdayRepository;
import com.topleague.predict.domain.model.Game;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RestGameRepository implements GameGetByIdRepository, GameGetByLeagueAndMatchdayRepository {

    private final GameFeignClient gameFeignClient;

    public RestGameRepository(GameFeignClient gameFeignClient) {
        this.gameFeignClient = gameFeignClient;
    }

    @Override
    public Optional<Game> getGameById(Integer gameId) {
        try {
            WebGameResponse response = gameFeignClient.getGameById(gameId);
            if (response == null) {
                return Optional.empty();
            }
            return Optional.of(mapToDomain(response));
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Game> getGamesByLeagueAndMatchday(Integer leagueId, Integer matchday) {
        try {
            List<WebGameResponse> response = gameFeignClient.getGamesByLeagueAndMatchday(leagueId, matchday);
            if (response == null) {
                return List.of();
            }
            return response.stream().map(this::mapToDomain).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    private Game mapToDomain(WebGameResponse dto) {
        return Game.builder()
                .id(dto.getId())
                .leagueId(dto.getLeagueId())
                .homeTeamId(dto.getHomeTeamId())
                .awayTeamId(dto.getAwayTeamId())
                .homeScore(dto.getHomeScore())
                .awayScore(dto.getAwayScore())
                .date(dto.getDate())
                .matchday(dto.getMatchday())
                .build();
    }
}
