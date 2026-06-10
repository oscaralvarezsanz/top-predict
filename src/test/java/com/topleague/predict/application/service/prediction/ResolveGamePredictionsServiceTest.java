package com.topleague.predict.application.service.prediction;

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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class ResolveGamePredictionsServiceTest {

    private final PredictionGetByGameRepository predictionGetByGameRepository = mock(PredictionGetByGameRepository.class);
    private final PredictionSaveRepository predictionSaveRepository = mock(PredictionSaveRepository.class);
    private final PredictionGetByGroupAndUserRepository predictionGetByGroupAndUserRepository = mock(PredictionGetByGroupAndUserRepository.class);
    private final GroupMemberGetByGroupAndUserRepository groupMemberGetByGroupAndUserRepository = mock(GroupMemberGetByGroupAndUserRepository.class);
    private final GroupMemberSaveRepository groupMemberSaveRepository = mock(GroupMemberSaveRepository.class);
    private final PredictionScoringStrategy scoringStrategy = mock(PredictionScoringStrategy.class);

    private final ResolveGamePredictionsService service = new ResolveGamePredictionsService(
            predictionGetByGameRepository,
            predictionSaveRepository,
            predictionGetByGroupAndUserRepository,
            groupMemberGetByGroupAndUserRepository,
            groupMemberSaveRepository,
            scoringStrategy
    );

    @Test
    void shouldResolvePredictionsAndRecalculateTotalPoints() {
        Integer gameId = 10;
        Integer homeScore = 2;
        Integer awayScore = 1;
        Integer groupId = 100;
        Integer userId = 50;

        Prediction prediction = Prediction.builder()
                .id(1)
                .gameId(gameId)
                .groupId(groupId)
                .userId(userId)
                .predictedHomeScore(2)
                .predictedAwayScore(1)
                .pointsEarned(0)
                .status(PredictionStatus.PENDING)
                .build();

        GroupMember groupMember = GroupMember.builder()
                .id(5)
                .groupId(groupId)
                .userId(userId)
                .alias("User1")
                .totalPoints(10)
                .build();

        Prediction updatedPrediction = prediction.toBuilder()
                .pointsEarned(3)
                .status(PredictionStatus.EVALUATED)
                .build();

        when(predictionGetByGameRepository.getPredictionsByGame(gameId))
                .thenReturn(Collections.singletonList(prediction));
        when(scoringStrategy.calculatePoints(eq(prediction), any(Result.class)))
                .thenReturn(3);
        when(predictionGetByGroupAndUserRepository.getPredictionsByGroupAndUser(groupId, userId))
                .thenReturn(Arrays.asList(updatedPrediction, Prediction.builder().pointsEarned(5).build()));
        when(groupMemberGetByGroupAndUserRepository.getGroupMember(groupId, userId))
                .thenReturn(Optional.of(groupMember));

        Result result = Result.builder()
                .gameId(gameId)
                .homeScore(homeScore)
                .awayScore(awayScore)
                .build();

        service.resolvePredictions(result);

        ArgumentCaptor<Prediction> predictionCaptor = ArgumentCaptor.forClass(Prediction.class);
        verify(predictionSaveRepository).savePrediction(predictionCaptor.capture());
        Prediction savedPrediction = predictionCaptor.getValue();
        assertThat(savedPrediction.getPointsEarned(), is(3));
        assertThat(savedPrediction.getStatus(), is(PredictionStatus.EVALUATED));

        ArgumentCaptor<GroupMember> memberCaptor = ArgumentCaptor.forClass(GroupMember.class);
        verify(groupMemberSaveRepository).saveGroupMember(memberCaptor.capture());
        GroupMember savedMember = memberCaptor.getValue();
        assertThat(savedMember.getTotalPoints(), is(8));
    }

    @Test
    void shouldDoNothingWhenNoPredictionsExistForGame() {
        Integer gameId = 10;
        Result result = Result.builder().gameId(gameId).homeScore(2).awayScore(1).build();

        when(predictionGetByGameRepository.getPredictionsByGame(gameId))
                .thenReturn(Collections.emptyList());

        service.resolvePredictions(result);

        verify(predictionGetByGameRepository).getPredictionsByGame(gameId);
        verifyNoInteractions(predictionSaveRepository,
                predictionGetByGroupAndUserRepository,
                groupMemberGetByGroupAndUserRepository,
                groupMemberSaveRepository);
    }

    @Test
    void shouldNotSaveGroupMemberWhenMemberDoesNotExist() {
        Integer gameId = 10;
        Integer groupId = 100;
        Integer userId = 50;

        Prediction prediction = Prediction.builder()
                .id(1)
                .gameId(gameId)
                .groupId(groupId)
                .userId(userId)
                .predictedHomeScore(2)
                .predictedAwayScore(1)
                .build();

        Result result = Result.builder().gameId(gameId).homeScore(2).awayScore(1).build();

        when(predictionGetByGameRepository.getPredictionsByGame(gameId))
                .thenReturn(Collections.singletonList(prediction));
        when(scoringStrategy.calculatePoints(eq(prediction), any(Result.class)))
                .thenReturn(3);
        when(predictionGetByGroupAndUserRepository.getPredictionsByGroupAndUser(groupId, userId))
                .thenReturn(Collections.emptyList());
        when(groupMemberGetByGroupAndUserRepository.getGroupMember(groupId, userId))
                .thenReturn(Optional.empty());

        service.resolvePredictions(result);

        verify(predictionSaveRepository).savePrediction(any(Prediction.class));
        verify(groupMemberGetByGroupAndUserRepository).getGroupMember(groupId, userId);
        verifyNoInteractions(groupMemberSaveRepository);
    }

    @Test
    void shouldHandleNullPointsEarnedWhenRecalculating() {
        Integer gameId = 10;
        Integer groupId = 100;
        Integer userId = 50;

        Prediction prediction = Prediction.builder()
                .id(1)
                .gameId(gameId)
                .groupId(groupId)
                .userId(userId)

                .predictedHomeScore(2)
                .predictedAwayScore(1)
                .build();

        GroupMember groupMember = GroupMember.builder()
                .id(5)
                .groupId(groupId)
                .userId(userId)
                .alias("User1")
                .totalPoints(10)
                .build();

        Result result = Result.builder().gameId(gameId).homeScore(2).awayScore(1).build();

        when(predictionGetByGameRepository.getPredictionsByGame(gameId))
                .thenReturn(Collections.singletonList(prediction));
        when(scoringStrategy.calculatePoints(eq(prediction), any(Result.class)))
                .thenReturn(3);
        when(predictionGetByGroupAndUserRepository.getPredictionsByGroupAndUser(groupId, userId))
                .thenReturn(Arrays.asList(
                        Prediction.builder().pointsEarned(null).build(),
                        Prediction.builder().pointsEarned(4).build()
                ));
        when(groupMemberGetByGroupAndUserRepository.getGroupMember(groupId, userId))
                .thenReturn(Optional.of(groupMember));

        service.resolvePredictions(result);

        ArgumentCaptor<GroupMember> memberCaptor = ArgumentCaptor.forClass(GroupMember.class);
        verify(groupMemberSaveRepository).saveGroupMember(memberCaptor.capture());
        GroupMember savedMember = memberCaptor.getValue();
        assertThat(savedMember.getTotalPoints(), is(4));
    }
}
