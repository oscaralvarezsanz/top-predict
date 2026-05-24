package com.topleague.predict.application.service.scoring;

import com.topleague.predict.domain.exception.PredictionException;
import com.topleague.predict.domain.model.Prediction;
import com.topleague.predict.domain.model.Result;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FixedScoringStrategyTest {

    private final FixedScoringStrategy strategy = new FixedScoringStrategy();

    private Prediction createPrediction(int home, int away) {
        return Prediction.builder()
                .gameId(1)
                .predictedHomeScore(home)
                .predictedAwayScore(away)
                .build();
    }

    private Result createResult(int home, int away) {
        return Result.builder()
                .gameId(1)
                .homeScore(home)
                .awayScore(away)
                .build();
    }

    @Test
    public void shouldReturn4ForExactMatch() {
        assertEquals(4, strategy.calculatePoints(createPrediction(2, 1), createResult(2, 1)));
        assertEquals(4, strategy.calculatePoints(createPrediction(0, 0), createResult(0, 0)));
        assertEquals(4, strategy.calculatePoints(createPrediction(3, 3), createResult(3, 3)));
    }

    @Test
    public void shouldReturn3ForCorrectDifferenceAndWinner() {
        // Correct difference and home win
        assertEquals(3, strategy.calculatePoints(createPrediction(2, 1), createResult(3, 2)));
        // Correct difference and away win
        assertEquals(3, strategy.calculatePoints(createPrediction(0, 2), createResult(1, 3)));
        // Correct difference and draw
        assertEquals(3, strategy.calculatePoints(createPrediction(1, 1), createResult(2, 2)));
    }

    @Test
    public void shouldReturn2ForCorrectWinnerIncorrectDifference() {
        // Home win with different difference (diff 1 vs diff 3)
        assertEquals(2, strategy.calculatePoints(createPrediction(2, 1), createResult(3, 0)));
        // Away win with different difference (diff -2 vs diff -1)
        assertEquals(2, strategy.calculatePoints(createPrediction(0, 2), createResult(1, 2)));
    }

    @Test
    public void shouldReturn0ForIncorrectWinner() {
        // Home win predicted, actual is draw
        assertEquals(0, strategy.calculatePoints(createPrediction(2, 1), createResult(1, 1)));
        // Home win predicted, actual is away win
        assertEquals(0, strategy.calculatePoints(createPrediction(2, 1), createResult(0, 2)));
        // Draw predicted, actual is home win
        assertEquals(0, strategy.calculatePoints(createPrediction(1, 1), createResult(2, 1)));
    }

    @Test
    public void shouldThrowExceptionForMismatchedGameIds() {
        Prediction pred = Prediction.builder().gameId(1).predictedHomeScore(2).predictedAwayScore(1).build();
        Result res = Result.builder().gameId(2).homeScore(2).awayScore(1).build();
        assertThrows(PredictionException.class, () -> strategy.calculatePoints(pred, res));
    }
}
