package com.topleague.predict.application.service.prediction;

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
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GroupGamePredictionsGetServiceTest {

    private final GroupMemberGetByGroupIdRepository groupMemberRepository = mock(GroupMemberGetByGroupIdRepository.class);
    private final GameGetByIdRepository gameRepository = mock(GameGetByIdRepository.class);
    private final PredictionGetByGroupAndGameRepository predictionRepository = mock(PredictionGetByGroupAndGameRepository.class);

    private final GroupGamePredictionsGetService service = new GroupGamePredictionsGetService(
            groupMemberRepository,
            gameRepository,
            predictionRepository
    );

    @Test
    void getGroupGamePredictionsShouldReturnUncensoredPredictionsWhenGameIsResolvedAndUserIsMember() {
        Integer groupId = 1;
        Integer gameId = 2;
        Integer userId = 42;

        GroupMember caller = GroupMember.builder().groupId(groupId).userId(userId).alias("caller").build();
        GroupMember other = GroupMember.builder().groupId(groupId).userId(99).alias("other").build();
        Game game = Game.builder().id(gameId).homeScore(2).awayScore(1).date(LocalDate.now().plusDays(2)).build(); // resolved early
        Prediction prediction1 = Prediction.builder().id(10).groupId(groupId).userId(userId).gameId(gameId).predictedHomeScore(2).predictedAwayScore(1).pointsEarned(3).build();
        Prediction prediction2 = Prediction.builder().id(11).groupId(groupId).userId(99).gameId(gameId).predictedHomeScore(1).predictedAwayScore(1).pointsEarned(0).build();
        List<Prediction> predictions = Arrays.asList(prediction1, prediction2);

        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Arrays.asList(caller, other));
        when(gameRepository.getGameById(gameId)).thenReturn(Optional.of(game));
        when(predictionRepository.getPredictionsByGroupAndGame(groupId, gameId)).thenReturn(predictions);

        List<GroupMemberPrediction> result = service.getGroupGamePredictions(groupId, gameId, userId);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getAlias(), is("caller"));
        assertThat(result.get(0).getPredictedHomeScore(), is(2));
        assertThat(result.get(1).getAlias(), is("other"));
        assertThat(result.get(1).getPredictedHomeScore(), is(1)); // Uncensored
        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verify(gameRepository).getGameById(gameId);
        verify(predictionRepository).getPredictionsByGroupAndGame(groupId, gameId);
    }

    @Test
    void getGroupGamePredictionsShouldReturnCensoredPredictionsWhenGameIsNotResolvedAndUserIsMember() {
        Integer groupId = 1;
        Integer gameId = 2;
        Integer userId = 42;

        GroupMember caller = GroupMember.builder().groupId(groupId).userId(userId).alias("caller").build();
        GroupMember other = GroupMember.builder().groupId(groupId).userId(99).alias("other").build();
        Game game = Game.builder().id(gameId).homeScore(null).awayScore(null).date(LocalDate.now().plusDays(2)).build(); // unresolved
        Prediction prediction1 = Prediction.builder().id(10).groupId(groupId).userId(userId).gameId(gameId).predictedHomeScore(2).predictedAwayScore(1).pointsEarned(null).build();
        Prediction prediction2 = Prediction.builder().id(11).groupId(groupId).userId(99).gameId(gameId).predictedHomeScore(1).predictedAwayScore(1).pointsEarned(null).build();
        List<Prediction> predictions = Arrays.asList(prediction1, prediction2);

        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Arrays.asList(caller, other));
        when(gameRepository.getGameById(gameId)).thenReturn(Optional.of(game));
        when(predictionRepository.getPredictionsByGroupAndGame(groupId, gameId)).thenReturn(predictions);

        List<GroupMemberPrediction> result = service.getGroupGamePredictions(groupId, gameId, userId);

        assertThat(result.size(), is(2));
        // Caller prediction is uncensored
        assertThat(result.get(0).getAlias(), is("caller"));
        assertThat(result.get(0).getPredictedHomeScore(), is(2));
        // Other prediction is censored
        assertThat(result.get(1).getAlias(), is("other"));
        assertThat(result.get(1).getPredictedHomeScore(), is(nullValue()));
        assertThat(result.get(1).getPredictedAwayScore(), is(nullValue()));
        assertThat(result.get(1).getPointsEarned(), is(nullValue()));
        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verify(gameRepository).getGameById(gameId);
        verify(predictionRepository).getPredictionsByGroupAndGame(groupId, gameId);
    }

    @Test
    void getGroupGamePredictionsShouldThrowForbiddenWhenCallerIsNotMember() {
        Integer groupId = 1;
        Integer gameId = 2;
        Integer userId = 42;

        GroupMember otherMember = GroupMember.builder().groupId(groupId).userId(99).build();

        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Collections.singletonList(otherMember));

        GroupMemberException exception = assertThrows(GroupMemberException.class, () ->
                service.getGroupGamePredictions(groupId, gameId, userId)
        );

        assertThat(exception.getErrorCode(), is(GroupMemberErrorCode.MEMBER_NOT_FOUND));
        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verifyNoInteractions(gameRepository, predictionRepository);
    }

    @Test
    void getGroupGamePredictionsShouldThrowNotFoundWhenGameDoesNotExist() {
        Integer groupId = 1;
        Integer gameId = 2;
        Integer userId = 42;

        GroupMember caller = GroupMember.builder().groupId(groupId).userId(userId).build();

        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Collections.singletonList(caller));
        when(gameRepository.getGameById(gameId)).thenReturn(Optional.empty());

        PredictionException exception = assertThrows(PredictionException.class, () ->
                service.getGroupGamePredictions(groupId, gameId, userId)
        );

        assertThat(exception.getErrorCode(), is(PredictionErrorCode.INVALID_PREDICTION_DATA));
        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verify(gameRepository).getGameById(gameId);
        verifyNoInteractions(predictionRepository);
    }
}
