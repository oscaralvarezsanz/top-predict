package com.topleague.predict.application.service.scoring;

import com.topleague.predict.domain.exception.PredictionErrorCode;
import com.topleague.predict.domain.exception.PredictionException;
import com.topleague.predict.domain.model.Prediction;
import com.topleague.predict.domain.model.Result;
import org.springframework.stereotype.Service;

@Service
public class FixedScoringStrategy implements PredictionScoringStrategy {
    private static final int EXACT_MATCH_POINTS = 4;
    private static final int CORRECT_DIFFERENCE_POINTS = 3;
    private static final int CORRECT_TENDENCY_POINTS = 2;
    private static final int NO_POINTS = 0;

    @Override
    public int calculatePoints(Prediction prediction, Result result) {
        if (prediction == null || result == null) {
            return NO_POINTS;
        }

        if (!prediction.getGameId().equals(result.getGameId())) {
            throw new PredictionException(PredictionErrorCode.MISMATCHED_GAME_IDS);
        }

        return isExactPrediction(prediction, result) ? EXACT_MATCH_POINTS
                : isCorrectDifference(prediction, result) ? CORRECT_DIFFERENCE_POINTS
                        : isCorrectTendency(prediction, result) ? CORRECT_TENDENCY_POINTS
                                : NO_POINTS;
    }

    private boolean isExactPrediction(Prediction prediction, Result result) {
        return prediction.getPredictedHomeScore() == result.getHomeScore()
                && prediction.getPredictedAwayScore() == result.getAwayScore();
    }

    private boolean isCorrectDifference(Prediction prediction, Result result) {
        return (prediction.getPredictedHomeScore() - prediction.getPredictedAwayScore())
                == (result.getHomeScore() - result.getAwayScore());
    }

    private boolean isCorrectTendency(Prediction prediction, Result result) {
        return Integer.signum(prediction.getPredictedHomeScore() - prediction.getPredictedAwayScore())
                == Integer.signum(result.getHomeScore() - result.getAwayScore());
    }
}
