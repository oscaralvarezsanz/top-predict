package com.topleague.predict.application.service.prediction;

import com.topleague.predict.application.port.out.game.GameGetByLeagueAndMatchdayRepository;
import com.topleague.predict.application.port.out.group.GroupGetByIdRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.application.port.out.prediction.PredictionGetByGroupAndGamesRepository;
import com.topleague.predict.domain.exception.GroupErrorCode;
import com.topleague.predict.domain.exception.GroupException;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.GroupMemberException;
import com.topleague.predict.domain.model.Game;
import com.topleague.predict.domain.model.Group;
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

class GroupMatchdayPredictionsGetServiceTest {

    private final GroupGetByIdRepository groupRepository = mock(GroupGetByIdRepository.class);
    private final GroupMemberGetByGroupIdRepository groupMemberRepository = mock(GroupMemberGetByGroupIdRepository.class);
    private final GameGetByLeagueAndMatchdayRepository gameRepository = mock(GameGetByLeagueAndMatchdayRepository.class);
    private final PredictionGetByGroupAndGamesRepository predictionRepository = mock(PredictionGetByGroupAndGamesRepository.class);

    private final GroupMatchdayPredictionsGetService service = new GroupMatchdayPredictionsGetService(
            groupRepository,
            groupMemberRepository,
            gameRepository,
            predictionRepository
    );

    @Test
    void getGroupMatchdayPredictionsShouldReturnUncensoredPredictionsWhenGameIsResolvedAndUserIsMember() {
        Integer groupId = 1;
        Integer matchday = 2;
        Integer userId = 42;
        Integer leagueId = 10;
        Integer gameId = 20;

        Group group = Group.builder().id(groupId).leagueId(leagueId).name("Test Group").build();
        GroupMember caller = GroupMember.builder().groupId(groupId).userId(userId).alias("caller").build();
        GroupMember other = GroupMember.builder().groupId(groupId).userId(99).alias("other").build();
        
        Game game = Game.builder().id(gameId).homeScore(2).awayScore(1).date(LocalDate.now().plusDays(2)).matchday(matchday).build();
        
        Prediction prediction1 = Prediction.builder().id(101).groupId(groupId).userId(userId).gameId(gameId).predictedHomeScore(2).predictedAwayScore(1).pointsEarned(3).build();
        Prediction prediction2 = Prediction.builder().id(102).groupId(groupId).userId(99).gameId(gameId).predictedHomeScore(1).predictedAwayScore(1).pointsEarned(0).build();
        List<Prediction> predictions = Arrays.asList(prediction1, prediction2);

        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Arrays.asList(caller, other));
        when(groupRepository.getGroupById(groupId)).thenReturn(Optional.of(group));
        when(gameRepository.getGamesByLeagueAndMatchday(leagueId, matchday)).thenReturn(Collections.singletonList(game));
        when(predictionRepository.getPredictionsByGroupAndGames(groupId, Collections.singletonList(gameId))).thenReturn(predictions);

        List<GroupMemberPrediction> result = service.getGroupMatchdayPredictions(groupId, matchday, userId);

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getAlias(), is("caller"));
        assertThat(result.get(0).getPredictedHomeScore(), is(2));
        assertThat(result.get(0).getGameId(), is(gameId));
        assertThat(result.get(1).getAlias(), is("other"));
        assertThat(result.get(1).getPredictedHomeScore(), is(1)); // Uncensored since game is resolved
        assertThat(result.get(1).getGameId(), is(gameId));

        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verify(groupRepository).getGroupById(groupId);
        verify(gameRepository).getGamesByLeagueAndMatchday(leagueId, matchday);
        verify(predictionRepository).getPredictionsByGroupAndGames(groupId, Collections.singletonList(gameId));
    }

    @Test
    void getGroupMatchdayPredictionsShouldReturnCensoredPredictionsWhenGameIsNotResolvedAndUserIsMember() {
        Integer groupId = 1;
        Integer matchday = 2;
        Integer userId = 42;
        Integer leagueId = 10;
        Integer gameId = 20;

        Group group = Group.builder().id(groupId).leagueId(leagueId).name("Test Group").build();
        GroupMember caller = GroupMember.builder().groupId(groupId).userId(userId).alias("caller").build();
        GroupMember other = GroupMember.builder().groupId(groupId).userId(99).alias("other").build();

        Game game = Game.builder().id(gameId).homeScore(null).awayScore(null).date(LocalDate.now().plusDays(2)).matchday(matchday).build();

        Prediction prediction1 = Prediction.builder().id(101).groupId(groupId).userId(userId).gameId(gameId).predictedHomeScore(2).predictedAwayScore(1).pointsEarned(null).build();
        Prediction prediction2 = Prediction.builder().id(102).groupId(groupId).userId(99).gameId(gameId).predictedHomeScore(1).predictedAwayScore(1).pointsEarned(null).build();
        List<Prediction> predictions = Arrays.asList(prediction1, prediction2);

        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Arrays.asList(caller, other));
        when(groupRepository.getGroupById(groupId)).thenReturn(Optional.of(group));
        when(gameRepository.getGamesByLeagueAndMatchday(leagueId, matchday)).thenReturn(Collections.singletonList(game));
        when(predictionRepository.getPredictionsByGroupAndGames(groupId, Collections.singletonList(gameId))).thenReturn(predictions);

        List<GroupMemberPrediction> result = service.getGroupMatchdayPredictions(groupId, matchday, userId);

        assertThat(result.size(), is(2));
        // Caller prediction is uncensored
        assertThat(result.get(0).getAlias(), is("caller"));
        assertThat(result.get(0).getPredictedHomeScore(), is(2));
        assertThat(result.get(0).getGameId(), is(gameId));
        // Other prediction is censored
        assertThat(result.get(1).getAlias(), is("other"));
        assertThat(result.get(1).getPredictedHomeScore(), is(nullValue()));
        assertThat(result.get(1).getPredictedAwayScore(), is(nullValue()));
        assertThat(result.get(1).getPointsEarned(), is(nullValue()));
        assertThat(result.get(1).getGameId(), is(gameId));

        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verify(groupRepository).getGroupById(groupId);
        verify(gameRepository).getGamesByLeagueAndMatchday(leagueId, matchday);
        verify(predictionRepository).getPredictionsByGroupAndGames(groupId, Collections.singletonList(gameId));
    }

    @Test
    void getGroupMatchdayPredictionsShouldThrowForbiddenWhenCallerIsNotMember() {
        Integer groupId = 1;
        Integer matchday = 2;
        Integer userId = 42;

        GroupMember otherMember = GroupMember.builder().groupId(groupId).userId(99).build();

        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Collections.singletonList(otherMember));

        GroupMemberException exception = assertThrows(GroupMemberException.class, () ->
                service.getGroupMatchdayPredictions(groupId, matchday, userId)
        );

        assertThat(exception.getErrorCode(), is(GroupMemberErrorCode.MEMBER_NOT_FOUND));
        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verifyNoInteractions(groupRepository, gameRepository, predictionRepository);
    }

    @Test
    void getGroupMatchdayPredictionsShouldThrowNotFoundWhenGroupDoesNotExist() {
        Integer groupId = 1;
        Integer matchday = 2;
        Integer userId = 42;

        GroupMember caller = GroupMember.builder().groupId(groupId).userId(userId).build();

        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Collections.singletonList(caller));
        when(groupRepository.getGroupById(groupId)).thenReturn(Optional.empty());

        GroupException exception = assertThrows(GroupException.class, () ->
                service.getGroupMatchdayPredictions(groupId, matchday, userId)
        );

        assertThat(exception.getErrorCode(), is(GroupErrorCode.GROUP_NOT_FOUND));
        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verify(groupRepository).getGroupById(groupId);
        verifyNoInteractions(gameRepository, predictionRepository);
    }

    @Test
    void getGroupMatchdayPredictionsShouldReturnEmptyListWhenNoGamesExist() {
        Integer groupId = 1;
        Integer matchday = 2;
        Integer userId = 42;
        Integer leagueId = 10;

        Group group = Group.builder().id(groupId).leagueId(leagueId).name("Test Group").build();
        GroupMember caller = GroupMember.builder().groupId(groupId).userId(userId).build();

        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Collections.singletonList(caller));
        when(groupRepository.getGroupById(groupId)).thenReturn(Optional.of(group));
        when(gameRepository.getGamesByLeagueAndMatchday(leagueId, matchday)).thenReturn(Collections.emptyList());

        List<GroupMemberPrediction> result = service.getGroupMatchdayPredictions(groupId, matchday, userId);

        assertThat(result.isEmpty(), is(true));
        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verify(groupRepository).getGroupById(groupId);
        verify(gameRepository).getGamesByLeagueAndMatchday(leagueId, matchday);
        verifyNoInteractions(predictionRepository);
    }
}
