package com.topleague.predict.infrastructure.in.controller;

import com.topleague.predict.application.port.in.group.CreateGroupUseCase;
import com.topleague.predict.application.port.in.group.GetGroupLeaderboardUseCase;
import com.topleague.predict.application.port.in.group.JoinGroupUseCase;
import com.topleague.predict.domain.model.AppUser;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupLeaderboard;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.mapper.GroupWebConverter;
import com.topleague.predict.infrastructure.in.mapper.LeaderboardWebConverter;
import com.topleague.predict.infrastructure.in.model.WebGroupCreateRequest;
import com.topleague.predict.infrastructure.in.model.WebGroupJoinRequest;
import com.topleague.predict.infrastructure.in.model.WebGroupResponse;
import com.topleague.predict.infrastructure.in.model.WebLeaderboardResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class GroupControllerTest {

    private static final Integer OWNER_ID = 42;
    private static final String USERNAME = "john_doe";

    private final CreateGroupUseCase createUseCase = mock(CreateGroupUseCase.class);
    private final GetGroupLeaderboardUseCase getGroupLeaderboardUseCase = mock(GetGroupLeaderboardUseCase.class);
    private final JoinGroupUseCase joinGroupUseCase = mock(JoinGroupUseCase.class);
    private final GroupWebConverter converter = mock(GroupWebConverter.class);
    private final LeaderboardWebConverter leaderboardConverter = mock(LeaderboardWebConverter.class);
    private final GroupController controller = new GroupController(createUseCase, getGroupLeaderboardUseCase, joinGroupUseCase, converter, leaderboardConverter);

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
}
