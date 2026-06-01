package com.topleague.predict.application.service.prediction;

import com.topleague.predict.application.port.in.prediction.SubmitPredictionUseCase;
import com.topleague.predict.application.port.out.game.GameGetByIdRepository;
import com.topleague.predict.application.port.out.group.GroupGetByIdRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.application.port.out.prediction.PredictionGetByGroupUserAndGameRepository;
import com.topleague.predict.application.port.out.prediction.PredictionSaveRepository;
import com.topleague.predict.domain.exception.GroupErrorCode;
import com.topleague.predict.domain.exception.GroupException;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.GroupMemberException;
import com.topleague.predict.domain.exception.PredictionErrorCode;
import com.topleague.predict.domain.exception.PredictionException;
import com.topleague.predict.domain.model.Game;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.domain.model.Prediction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PredictionSubmitService implements SubmitPredictionUseCase {

    private final GroupGetByIdRepository groupGetByIdRepository;
    private final GroupMemberGetByGroupIdRepository groupMemberGetByGroupIdRepository;
    private final GameGetByIdRepository gameGetByIdRepository;
    private final PredictionGetByGroupUserAndGameRepository predictionGetByGroupUserAndGameRepository;
    private final PredictionSaveRepository predictionSaveRepository;

    public PredictionSubmitService(
            GroupGetByIdRepository groupGetByIdRepository,
            GroupMemberGetByGroupIdRepository groupMemberGetByGroupIdRepository,
            GameGetByIdRepository gameGetByIdRepository,
            PredictionGetByGroupUserAndGameRepository predictionGetByGroupUserAndGameRepository,
            PredictionSaveRepository predictionSaveRepository) {
        this.groupGetByIdRepository = groupGetByIdRepository;
        this.groupMemberGetByGroupIdRepository = groupMemberGetByGroupIdRepository;
        this.gameGetByIdRepository = gameGetByIdRepository;
        this.predictionGetByGroupUserAndGameRepository = predictionGetByGroupUserAndGameRepository;
        this.predictionSaveRepository = predictionSaveRepository;
    }

    @Override
    @Transactional
    public Prediction submitPrediction(Prediction prediction) {
        if (prediction.getPredictedHomeScore() == null || prediction.getPredictedHomeScore() < 0
                || prediction.getPredictedAwayScore() == null || prediction.getPredictedAwayScore() < 0) {
            throw new PredictionException(PredictionErrorCode.INVALID_PREDICTION_DATA, 
                "Predicted scores must be non-negative");
        }

        final Group group = groupGetByIdRepository.getGroupById(prediction.getGroupId())
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        if (!isUserAGroupMember(prediction)) {
            throw new GroupMemberException(GroupMemberErrorCode.MEMBER_NOT_FOUND);
        }

        final Game game = gameGetByIdRepository.getGameById(prediction.getGameId())
                .orElseThrow(() -> new PredictionException(PredictionErrorCode.INVALID_PREDICTION_DATA, "Match not found"));

        if (!game.getLeagueId().equals(group.getLeagueId())) {
            throw new PredictionException(PredictionErrorCode.INVALID_PREDICTION_DATA, "Match league does not match group league");
        }

        if (!LocalDate.now().isBefore(game.getDate())) {
            throw new PredictionException(PredictionErrorCode.PREDICTION_LOCKED, "Prediction is locked (match is today or has passed)");
        }

        final Prediction finalPrediction = predictionGetByGroupUserAndGameRepository
                .getPredictionByGroupUserAndGame(prediction.getGroupId(), prediction.getUserId(), prediction.getGameId())
                .map(existing -> existing.toBuilder()
                        .predictedHomeScore(prediction.getPredictedHomeScore())
                        .predictedAwayScore(prediction.getPredictedAwayScore())
                        .build())
                .orElse(prediction);

        return predictionSaveRepository.savePrediction(finalPrediction);
    }

    private boolean isUserAGroupMember(Prediction prediction) {
        List<GroupMember> members = groupMemberGetByGroupIdRepository.getGroupMembersByGroupId(prediction.getGroupId());
        return members.stream()
                .anyMatch(member -> member.getUserId().equals(prediction.getUserId()));
    }
}
