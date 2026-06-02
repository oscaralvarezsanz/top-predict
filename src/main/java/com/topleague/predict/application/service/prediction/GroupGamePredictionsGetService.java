package com.topleague.predict.application.service.prediction;

import com.topleague.predict.application.port.in.prediction.GetGroupGamePredictionsUseCase;
import com.topleague.predict.application.port.out.game.GameGetByIdRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.application.port.out.prediction.PredictionGetByGroupAndGameRepository;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.GroupMemberException;
import com.topleague.predict.domain.exception.PredictionErrorCode;
import com.topleague.predict.domain.exception.PredictionException;
import com.topleague.predict.domain.model.Game;
import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.domain.model.GroupMemberPrediction;
import com.topleague.predict.domain.model.Prediction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GroupGamePredictionsGetService implements GetGroupGamePredictionsUseCase {

    private final GroupMemberGetByGroupIdRepository groupMemberGetByGroupIdRepository;
    private final GameGetByIdRepository gameGetByIdRepository;
    private final PredictionGetByGroupAndGameRepository predictionGetByGroupAndGameRepository;

    public GroupGamePredictionsGetService(
            GroupMemberGetByGroupIdRepository groupMemberGetByGroupIdRepository,
            GameGetByIdRepository gameGetByIdRepository,
            PredictionGetByGroupAndGameRepository predictionGetByGroupAndGameRepository) {
        this.groupMemberGetByGroupIdRepository = groupMemberGetByGroupIdRepository;
        this.gameGetByIdRepository = gameGetByIdRepository;
        this.predictionGetByGroupAndGameRepository = predictionGetByGroupAndGameRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberPrediction> getGroupGamePredictions(Integer groupId, Integer gameId, Integer userId) {
        List<GroupMember> members = groupMemberGetByGroupIdRepository.getGroupMembersByGroupId(groupId);
        
        if (!members.stream().anyMatch(member -> member.getUserId().equals(userId))) {
            throw new GroupMemberException(GroupMemberErrorCode.MEMBER_NOT_FOUND);
        }

        final Game game = gameGetByIdRepository.getGameById(gameId)
                .orElseThrow(() -> new PredictionException(
                        PredictionErrorCode.INVALID_PREDICTION_DATA, "Match not found"));

        List<Prediction> predictions = predictionGetByGroupAndGameRepository
                .getPredictionsByGroupAndGame(groupId, gameId);
                
        boolean isResolved = game.getHomeScore() != null && game.getAwayScore() != null;

        return members.stream().map(member -> hideUnresolvedPredictionScore(userId, predictions, isResolved, member))
                .toList();
    }

    private GroupMemberPrediction hideUnresolvedPredictionScore(Integer userId, 
                                                                List<Prediction> predictions,
                                                                boolean isResolved, 
                                                                GroupMember member) {
        Optional<Prediction> predictionOpt = predictions.stream()
                .filter(p -> p.getUserId().equals(member.getUserId()))
                .findFirst();

        boolean hideScores = !isResolved && !member.getUserId().equals(userId);

        return GroupMemberPrediction.builder()
                .userId(member.getUserId())
                .alias(member.getAlias())
                .predictedHomeScore(!hideScores && predictionOpt.isPresent() ? predictionOpt.get().getPredictedHomeScore() : null)
                .predictedAwayScore(!hideScores && predictionOpt.isPresent() ? predictionOpt.get().getPredictedAwayScore() : null)
                .pointsEarned(!hideScores && predictionOpt.isPresent() ? predictionOpt.get().getPointsEarned() : 0)
                .build();
    }
}
