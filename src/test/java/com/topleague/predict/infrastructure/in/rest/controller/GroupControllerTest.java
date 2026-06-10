package com.topleague.predict.infrastructure.in.rest.controller;

import com.topleague.predict.application.port.in.group.CreateGroupUseCase;
import com.topleague.predict.application.port.in.group.GetGroupLeaderboardUseCase;
import com.topleague.predict.application.port.in.group.JoinGroupUseCase;
import com.topleague.predict.application.port.in.prediction.GetGroupGamePredictionsUseCase;
import com.topleague.predict.application.port.in.prediction.GetGroupMatchdayPredictionsUseCase;
import com.topleague.predict.domain.model.AppUser;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupLeaderboard;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.rest.controller.GroupController;
import com.topleague.predict.infrastructure.in.rest.mapper.GroupMemberPredictionWebConverter;
import com.topleague.predict.infrastructure.in.rest.mapper.GroupWebConverter;
import com.topleague.predict.infrastructure.in.rest.mapper.LeaderboardWebConverter;
import com.topleague.predict.infrastructure.in.model.WebGroupCreateRequest;
import com.topleague.predict.infrastructure.in.model.WebGroupJoinRequest;
import com.topleague.predict.infrastructure.in.model.WebGroupMemberPrediction;
import com.topleague.predict.infrastructure.in.model.WebGroupResponse;
import com.topleague.predict.infrastructure.in.model.WebLeaderboardResponse;
import com.topleague.predict.infrastructure.in.model.WebMatchdayPredictionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class GroupControllerTest {

    private static final Integer OWNER_ID = 42;
    private static final String USERNAME = "john_doe";

    private final CreateGroupUseCase createUseCase = mock(CreateGroupUseCase.class);
    private final GetGroupLeaderboardUseCase getGroupLeaderboardUseCase = mock(GetGroupLeaderboardUseCase.class);
    private final JoinGroupUseCase joinGroupUseCase = mock(JoinGroupUseCase.class);
    private final GetGroupGamePredictionsUseCase getGroupGamePredictionsUseCase = mock(GetGroupGamePredictionsUseCase.class);
    private final GetGroupMatchdayPredictionsUseCase getGroupMatchdayPredictionsUseCase = mock(GetGroupMatchdayPredictionsUseCase.class);
    private final GroupWebConverter converter = mock(GroupWebConverter.class);
    private final LeaderboardWebConverter leaderboardConverter = mock(LeaderboardWebConverter.class);
    private final GroupMemberPredictionWebConverter groupMemberPredictionConverter = mock(GroupMemberPredictionWebConverter.class);
    private final GroupController controller = new GroupController(
            createUseCase, getGroupLeaderboardUseCase, joinGroupUseCase, getGroupGamePredictionsUseCase, getGroupMatchdayPredictionsUseCase,
            converter, leaderboardConverter, groupMemberPredictionConverter
    );

    @BeforeEach
    void setup() {
        AppUserDetails principal = new AppUserDetails(AppUser.builder().id(OWNER_ID).username(USERNAME).build());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createGroupShouldInjectOwnerIdFromTokenAndReturn201Created() {

        WebGroupCreateRequest webRequest = WebGroupCreateRequest.builder().name("Group A").leagueId(5).build();
        Group groupFromConverter = Group.builder().name("Group A").leagueId(5).build();
        Group groupWithOwner = Group.builder().name("Group A").leagueId(5).ownerId(OWNER_ID).build();
        Group createdGroup = Group.builder().id(10).name("Group A").leagueId(5).ownerId(OWNER_ID).build();
        WebGroupResponse webResponse = WebGroupResponse.builder().id(10).name("Group A").ownerId(OWNER_ID).build();

        when(converter.toDomain(webRequest)).thenReturn(groupFromConverter);
        when(createUseCase.createGroup(groupWithOwner, USERNAME)).thenReturn(createdGroup);
        when(converter.toWebResponse(createdGroup)).thenReturn(webResponse);

        ResponseEntity<WebGroupResponse> response = controller.createGroup(webRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody(), is(webResponse));

        verify(converter).toDomain(webRequest);
        verify(createUseCase).createGroup(groupWithOwner, USERNAME);
        verify(converter).toWebResponse(createdGroup);
    }

    @Test
    void getGroupLeaderboardShouldReturn200OkWithLeaderboard() {
        Integer groupId = 10;
        GroupLeaderboard domainLeaderboard = GroupLeaderboard.builder()
                .groupId(groupId)
                .groupName("My Group")
                .build();
        WebLeaderboardResponse webResponse = WebLeaderboardResponse.builder()
                .groupId(groupId)
                .groupName("My Group")
                .build();

        when(getGroupLeaderboardUseCase.getGroupLeaderboard(groupId, OWNER_ID)).thenReturn(domainLeaderboard);
        when(leaderboardConverter.toWebResponse(domainLeaderboard)).thenReturn(webResponse);

        ResponseEntity<WebLeaderboardResponse> response = controller.getGroupLeaderboard(groupId);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(webResponse));

        verify(getGroupLeaderboardUseCase).getGroupLeaderboard(groupId, OWNER_ID);
        verify(leaderboardConverter).toWebResponse(domainLeaderboard);
    }

    @Test
    void joinGroupShouldReturn200OkWithJoinedGroup() {
        String code = "INVITE88";
        WebGroupJoinRequest webRequest = WebGroupJoinRequest.builder().inviteCode(code).build();
        Group domainGroup = Group.builder().id(10).name("My Group").inviteCode(code).build();
        WebGroupResponse webResponse = WebGroupResponse.builder().id(10).name("My Group").inviteCode(code).build();

        when(joinGroupUseCase.joinGroup(code, OWNER_ID, USERNAME)).thenReturn(domainGroup);
        when(converter.toWebResponse(domainGroup)).thenReturn(webResponse);

        ResponseEntity<WebGroupResponse> response = controller.joinGroup(webRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(webResponse));

        verify(joinGroupUseCase).joinGroup(code, OWNER_ID, USERNAME);
        verify(converter).toWebResponse(domainGroup);
    }

    @Test
    void getGroupGamePredictionsShouldReturnPredictionsWith200Ok() {
        Integer groupId = 10;
        Integer gameId = 20;

        com.topleague.predict.domain.model.GroupMemberPrediction pred1 = com.topleague.predict.domain.model.GroupMemberPrediction.builder().userId(OWNER_ID).alias("caller").predictedHomeScore(2).predictedAwayScore(1).build();
        com.topleague.predict.domain.model.GroupMemberPrediction pred2 = com.topleague.predict.domain.model.GroupMemberPrediction.builder().userId(99).alias("other").predictedHomeScore(null).predictedAwayScore(null).build();
        List<com.topleague.predict.domain.model.GroupMemberPrediction> predictions = Arrays.asList(pred1, pred2);

        WebGroupMemberPrediction response1 = WebGroupMemberPrediction.builder().userId(OWNER_ID).alias("caller").predictedHomeScore(2).predictedAwayScore(1).build();
        WebGroupMemberPrediction response2 = WebGroupMemberPrediction.builder().userId(99).alias("other").predictedHomeScore(null).predictedAwayScore(null).build();

        when(getGroupGamePredictionsUseCase.getGroupGamePredictions(groupId, gameId, OWNER_ID)).thenReturn(predictions);
        when(groupMemberPredictionConverter.toWebResponse(pred1)).thenReturn(response1);
        when(groupMemberPredictionConverter.toWebResponse(pred2)).thenReturn(response2);

        ResponseEntity<List<WebGroupMemberPrediction>> response = controller.getGroupGamePredictions(groupId, gameId);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(Arrays.asList(response1, response2)));

        verify(getGroupGamePredictionsUseCase).getGroupGamePredictions(groupId, gameId, OWNER_ID);
        verify(groupMemberPredictionConverter).toWebResponse(pred1);
        verify(groupMemberPredictionConverter).toWebResponse(pred2);
    }

    @Test
    void getGroupMatchdayPredictionsShouldReturnPredictionsWith200Ok() {
        Integer groupId = 10;
        Integer matchday = 2;

        com.topleague.predict.domain.model.GroupMemberPrediction pred1 = com.topleague.predict.domain.model.GroupMemberPrediction.builder().gameId(20).userId(OWNER_ID).alias("caller").predictedHomeScore(2).predictedAwayScore(1).build();
        com.topleague.predict.domain.model.GroupMemberPrediction pred2 = com.topleague.predict.domain.model.GroupMemberPrediction.builder().gameId(20).userId(99).alias("other").predictedHomeScore(null).predictedAwayScore(null).build();
        List<com.topleague.predict.domain.model.GroupMemberPrediction> predictions = Arrays.asList(pred1, pred2);

        WebMatchdayPredictionResponse response1 = WebMatchdayPredictionResponse.builder().gameId(20).userId(OWNER_ID).alias("caller").predictedHomeScore(2).predictedAwayScore(1).build();
        WebMatchdayPredictionResponse response2 = WebMatchdayPredictionResponse.builder().gameId(20).userId(99).alias("other").predictedHomeScore(null).predictedAwayScore(null).build();

        when(getGroupMatchdayPredictionsUseCase.getGroupMatchdayPredictions(groupId, matchday, OWNER_ID)).thenReturn(predictions);
        when(groupMemberPredictionConverter.toWebMatchdayResponse(pred1)).thenReturn(response1);
        when(groupMemberPredictionConverter.toWebMatchdayResponse(pred2)).thenReturn(response2);

        ResponseEntity<List<WebMatchdayPredictionResponse>> response = controller.getGroupMatchdayPredictions(groupId, matchday);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(Arrays.asList(response1, response2)));

        verify(getGroupMatchdayPredictionsUseCase).getGroupMatchdayPredictions(groupId, matchday, OWNER_ID);
        verify(groupMemberPredictionConverter).toWebMatchdayResponse(pred1);
        verify(groupMemberPredictionConverter).toWebMatchdayResponse(pred2);
    }
}
