package com.topleague.predict.application.service.prediction;

import com.topleague.predict.application.port.in.prediction.ResolveGamePredictionsUseCase;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupAndUserRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberSaveRepository;
import com.topleague.predict.application.port.out.prediction.PredictionGetByGameRepository;
import com.topleague.predict.application.port.out.prediction.PredictionGetByGroupAndUserRepository;
import com.topleague.predict.application.port.out.prediction.PredictionSaveRepository;
import com.topleague.predict.application.service.scoring.PredictionScoringStrategy;
import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.domain.model.Prediction;
import com.topleague.predict.domain.model.PredictionStatus;
import com.topleague.predict.domain.model.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResolveGamePredictionsService implements ResolveGamePredictionsUseCase {

    private final PredictionGetByGameRepository predictionGetByGameRepository;
    private final PredictionSaveRepository predictionSaveRepository;
    private final PredictionGetByGroupAndUserRepository predictionGetByGroupAndUserRepository;
    private final GroupMemberGetByGroupAndUserRepository groupMemberGetByGroupAndUserRepository;
    private final GroupMemberSaveRepository groupMemberSaveRepository;
    private final PredictionScoringStrategy scoringStrategy;

    public ResolveGamePredictionsService(
            PredictionGetByGameRepository predictionGetByGameRepository,
            PredictionSaveRepository predictionSaveRepository,
            PredictionGetByGroupAndUserRepository predictionGetByGroupAndUserRepository,
            GroupMemberGetByGroupAndUserRepository groupMemberGetByGroupAndUserRepository,
            GroupMemberSaveRepository groupMemberSaveRepository,
            PredictionScoringStrategy scoringStrategy) {
        this.predictionGetByGameRepository = predictionGetByGameRepository;
        this.predictionSaveRepository = predictionSaveRepository;
        this.predictionGetByGroupAndUserRepository = predictionGetByGroupAndUserRepository;
        this.groupMemberGetByGroupAndUserRepository = groupMemberGetByGroupAndUserRepository;
        this.groupMemberSaveRepository = groupMemberSaveRepository;
        this.scoringStrategy = scoringStrategy;
    }

    @Override
    @Transactional
    public void resolvePredictions(Result result) {
        List<Prediction> predictions = predictionGetByGameRepository.getPredictionsByGame(result.getGameId());

        for (Prediction prediction : predictions) {
            int points = scoringStrategy.calculatePoints(prediction, result);
            Prediction updatedPrediction = prediction.toBuilder()
                    .pointsEarned(points)
                    .status(PredictionStatus.EVALUATED)
                    .build();
            predictionSaveRepository.savePrediction(updatedPrediction);

            recalculateMemberTotalPoints(prediction.getGroupId(), prediction.getUserId());
        }
    }

    private void recalculateMemberTotalPoints(Integer groupId, Integer userId) {
        List<Prediction> allPredictions = predictionGetByGroupAndUserRepository
                .getPredictionsByGroupAndUser(groupId, userId);

        int totalPoints = allPredictions.stream()
                .mapToInt(p -> p.getPointsEarned() != null ? p.getPointsEarned() : 0)
                .sum();

        groupMemberGetByGroupAndUserRepository.getGroupMember(groupId, userId)
                .ifPresent(member -> {
                    GroupMember updatedMember = member.toBuilder()
                            .totalPoints(totalPoints)
                            .build();
                    groupMemberSaveRepository.saveGroupMember(updatedMember);
                });
    }
}
