package com.topleague.predict.application.service.prediction;

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
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PredictionSubmitServiceTest {

    private final GroupGetByIdRepository groupRepository = mock(GroupGetByIdRepository.class);
    private final GroupMemberGetByGroupIdRepository groupMemberRepository = mock(GroupMemberGetByGroupIdRepository.class);
    private final GameGetByIdRepository gameRepository = mock(GameGetByIdRepository.class);
    private final PredictionGetByGroupUserAndGameRepository predictionGetRepository = mock(PredictionGetByGroupUserAndGameRepository.class);
    private final PredictionSaveRepository predictionSaveRepository = mock(PredictionSaveRepository.class);

    private final PredictionSubmitService service = new PredictionSubmitService(
            groupRepository,
            groupMemberRepository,
            gameRepository,
            predictionGetRepository,
            predictionSaveRepository
    );

    @Test
    void submitPredictionShouldThrowBadRequestWhenScoresAreNegative() {
        Prediction prediction = Prediction.builder()
                .predictedHomeScore(-1)
                .predictedAwayScore(2)
                .build();

        PredictionException exception = assertThrows(PredictionException.class, () ->
                service.submitPrediction(prediction)
        );

        assertThat(exception.getErrorCode(), is(PredictionErrorCode.INVALID_PREDICTION_DATA));
        verifyNoInteractions(groupRepository, groupMemberRepository, gameRepository, predictionGetRepository, predictionSaveRepository);
    }

    @Test
    void submitPredictionShouldThrowGroupNotFoundWhenGroupDoesNotExist() {
        Prediction prediction = Prediction.builder()
                .groupId(1)
                .predictedHomeScore(1)
                .predictedAwayScore(0)
                .build();

        when(groupRepository.getGroupById(1)).thenReturn(Optional.empty());

        GroupException exception = assertThrows(GroupException.class, () ->
                service.submitPrediction(prediction)
        );

        assertThat(exception.getErrorCode(), is(GroupErrorCode.GROUP_NOT_FOUND));
        verify(groupRepository).getGroupById(1);
        verifyNoInteractions(groupMemberRepository, gameRepository, predictionGetRepository, predictionSaveRepository);
    }

    @Test
    void submitPredictionShouldThrowForbiddenWhenUserIsNotGroupMember() {
        Prediction prediction = Prediction.builder()
                .groupId(1)
                .userId(100)
                .predictedHomeScore(1)
                .predictedAwayScore(0)
                .build();

        Group group = Group.builder().id(1).leagueId(10).build();
        GroupMember member = GroupMember.builder().userId(200).build();

        when(groupRepository.getGroupById(1)).thenReturn(Optional.of(group));
        when(groupMemberRepository.getGroupMembersByGroupId(1)).thenReturn(Arrays.asList(member));

        GroupMemberException exception = assertThrows(GroupMemberException.class, () ->
                service.submitPrediction(prediction)
        );

        assertThat(exception.getErrorCode(), is(GroupMemberErrorCode.MEMBER_NOT_FOUND));
        verify(groupRepository).getGroupById(1);
        verify(groupMemberRepository).getGroupMembersByGroupId(1);
        verifyNoInteractions(gameRepository, predictionGetRepository, predictionSaveRepository);
    }

    @Test
    void submitPredictionShouldThrowBadRequestWhenMatchDoesNotExist() {
        Prediction prediction = Prediction.builder()
                .groupId(1)
                .userId(100)
                .gameId(5)
                .predictedHomeScore(1)
                .predictedAwayScore(0)
                .build();

        Group group = Group.builder().id(1).leagueId(10).build();
        GroupMember member = GroupMember.builder().userId(100).build();

        when(groupRepository.getGroupById(1)).thenReturn(Optional.of(group));
        when(groupMemberRepository.getGroupMembersByGroupId(1)).thenReturn(Arrays.asList(member));
        when(gameRepository.getGameById(5)).thenReturn(Optional.empty());

        PredictionException exception = assertThrows(PredictionException.class, () ->
                service.submitPrediction(prediction)
        );

        assertThat(exception.getErrorCode(), is(PredictionErrorCode.INVALID_PREDICTION_DATA));
        verify(groupRepository).getGroupById(1);
        verify(groupMemberRepository).getGroupMembersByGroupId(1);
        verify(gameRepository).getGameById(5);
        verifyNoInteractions(predictionGetRepository, predictionSaveRepository);
    }

    @Test
    void submitPredictionShouldThrowBadRequestWhenMatchLeagueDoesNotMatchGroupLeague() {
        Prediction prediction = Prediction.builder()
                .groupId(1)
                .userId(100)
                .gameId(5)
                .predictedHomeScore(1)
                .predictedAwayScore(0)
                .build();

        Group group = Group.builder().id(1).leagueId(10).build();
        GroupMember member = GroupMember.builder().userId(100).build();
        Game game = Game.builder().id(5).leagueId(20).build(); // mismatched leagueId 20 vs 10

        when(groupRepository.getGroupById(1)).thenReturn(Optional.of(group));
        when(groupMemberRepository.getGroupMembersByGroupId(1)).thenReturn(Arrays.asList(member));
        when(gameRepository.getGameById(5)).thenReturn(Optional.of(game));

        PredictionException exception = assertThrows(PredictionException.class, () ->
                service.submitPrediction(prediction)
        );

        assertThat(exception.getErrorCode(), is(PredictionErrorCode.INVALID_PREDICTION_DATA));
        verify(groupRepository).getGroupById(1);
        verify(groupMemberRepository).getGroupMembersByGroupId(1);
        verify(gameRepository).getGameById(5);
        verifyNoInteractions(predictionGetRepository, predictionSaveRepository);
    }

    @Test
    void submitPredictionShouldThrowForbiddenWhenPreviousDayConstraintIsViolated() {
        Prediction prediction = Prediction.builder()
                .groupId(1)
                .userId(100)
                .gameId(5)
                .predictedHomeScore(1)
                .predictedAwayScore(0)
                .build();

        Group group = Group.builder().id(1).leagueId(10).build();
        GroupMember member = GroupMember.builder().userId(100).build();
        Game game = Game.builder().id(5).leagueId(10).date(LocalDate.now()).build();

        when(groupRepository.getGroupById(1)).thenReturn(Optional.of(group));
        when(groupMemberRepository.getGroupMembersByGroupId(1)).thenReturn(Arrays.asList(member));
        when(gameRepository.getGameById(5)).thenReturn(Optional.of(game));

        PredictionException exception = assertThrows(PredictionException.class, () ->
                service.submitPrediction(prediction)
        );

        assertThat(exception.getErrorCode(), is(PredictionErrorCode.PREDICTION_LOCKED));
        verify(groupRepository).getGroupById(1);
        verify(groupMemberRepository).getGroupMembersByGroupId(1);
        verify(gameRepository).getGameById(5);
        verifyNoInteractions(predictionGetRepository, predictionSaveRepository);
    }

    @Test
    void submitPredictionShouldCreateNewPredictionWhenNoneExists() {
        Prediction prediction = Prediction.builder()
                .groupId(1)
                .userId(100)
                .gameId(5)
                .predictedHomeScore(1)
                .predictedAwayScore(0)
                .build();

        Group group = Group.builder().id(1).leagueId(10).build();
        GroupMember member = GroupMember.builder().userId(100).build();
        Game game = Game.builder().id(5).leagueId(10).date(LocalDate.now().plusDays(1)).build();

        when(groupRepository.getGroupById(1)).thenReturn(Optional.of(group));
        when(groupMemberRepository.getGroupMembersByGroupId(1)).thenReturn(Arrays.asList(member));
        when(gameRepository.getGameById(5)).thenReturn(Optional.of(game));
        when(predictionGetRepository.getPredictionByGroupUserAndGame(1, 100, 5)).thenReturn(Optional.empty());
        when(predictionSaveRepository.savePrediction(prediction)).thenReturn(prediction);

        Prediction result = service.submitPrediction(prediction);

        assertThat(result, is(prediction));
        verify(groupRepository).getGroupById(1);
        verify(groupMemberRepository).getGroupMembersByGroupId(1);
        verify(gameRepository).getGameById(5);
        verify(predictionGetRepository).getPredictionByGroupUserAndGame(1, 100, 5);
        verify(predictionSaveRepository).savePrediction(prediction);
    }

    @Test
    void submitPredictionShouldUpdateExistingPredictionWhenOneAlreadyExists() {
        Prediction prediction = Prediction.builder()
                .groupId(1)
                .userId(100)
                .gameId(5)
                .predictedHomeScore(3)
                .predictedAwayScore(2)
                .build();

        Group group = Group.builder().id(1).leagueId(10).build();
        GroupMember member = GroupMember.builder().userId(100).build();
        Game game = Game.builder().id(5).leagueId(10).date(LocalDate.now().plusDays(1)).build(); // unlocked

        Prediction existingPrediction = Prediction.builder()
                .id(999)
                .groupId(1)
                .userId(100)
                .gameId(5)
                .predictedHomeScore(1)
                .predictedAwayScore(1)
                .build();

        Prediction expectedUpdated = existingPrediction.toBuilder()
                .predictedHomeScore(3)
                .predictedAwayScore(2)
                .build();

        when(groupRepository.getGroupById(1)).thenReturn(Optional.of(group));
        when(groupMemberRepository.getGroupMembersByGroupId(1)).thenReturn(Arrays.asList(member));
        when(gameRepository.getGameById(5)).thenReturn(Optional.of(game));
        when(predictionGetRepository.getPredictionByGroupUserAndGame(1, 100, 5)).thenReturn(Optional.of(existingPrediction));
        when(predictionSaveRepository.savePrediction(expectedUpdated)).thenReturn(expectedUpdated);

        Prediction result = service.submitPrediction(prediction);

        assertThat(result, is(expectedUpdated));
        verify(groupRepository).getGroupById(1);
        verify(groupMemberRepository).getGroupMembersByGroupId(1);
        verify(gameRepository).getGameById(5);
        verify(predictionGetRepository).getPredictionByGroupUserAndGame(1, 100, 5);
        verify(predictionSaveRepository).savePrediction(expectedUpdated);
    }
}
