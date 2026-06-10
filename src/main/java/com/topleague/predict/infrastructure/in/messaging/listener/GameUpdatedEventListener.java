package com.topleague.predict.infrastructure.in.messaging.listener;

import com.topleague.predict.application.port.in.prediction.ResolveGamePredictionsUseCase;
import com.topleague.predict.infrastructure.in.messaging.dto.GameUpdatedEventPayload;
import com.topleague.predict.domain.model.Result;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class GameUpdatedEventListener {
    private final ResolveGamePredictionsUseCase resolveGamePredictionsUseCase;

    public GameUpdatedEventListener(ResolveGamePredictionsUseCase resolveGamePredictionsUseCase) {
        this.resolveGamePredictionsUseCase = resolveGamePredictionsUseCase;
    }

    @RabbitListener(queues = "topleague.game.updated.top-predict-queue")
    public void handleGameUpdated(GameUpdatedEventPayload event) {
        if (event.homeScore() != null && event.awayScore() != null) {
            resolveGamePredictionsUseCase.resolvePredictions(
                    Result.builder()
                            .gameId(event.gameId())
                            .homeScore(event.homeScore())
                            .awayScore(event.awayScore())
                            .build()
            );
        }
    }
}
