package com.topleague.predict.infrastructure.in.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topleague.predict.domain.model.AppUser;
import com.topleague.predict.domain.model.UserRole;
import com.topleague.predict.domain.model.PredictionStatus;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.model.WebGroupCreateRequest;
import com.topleague.predict.infrastructure.in.model.WebGroupJoinRequest;
import com.topleague.predict.infrastructure.out.db.group.JpaGroupRepository;
import com.topleague.predict.infrastructure.out.db.groupmember.JpaGroupMemberRepository;
import com.topleague.predict.infrastructure.out.db.model.GroupEntity;
import com.topleague.predict.infrastructure.out.db.model.GroupMemberEntity;
import com.topleague.predict.infrastructure.out.db.model.PredictionEntity;
import com.topleague.predict.infrastructure.out.db.prediction.JpaPredictionRepository;
import com.topleague.predict.infrastructure.out.rest.game.GameFeignClient;
import com.topleague.predict.infrastructure.out.rest.game.WebGameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class GroupE2EIT {

    private static final Integer USER_ID = 42;
    private static final String USERNAME = "john_doe";

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private JpaGroupRepository jpaGroupRepository;

    @MockitoSpyBean
    private JpaGroupMemberRepository jpaGroupMemberRepository;

    @MockitoSpyBean
    private JpaPredictionRepository jpaPredictionRepository;

    @MockitoBean
    private GameFeignClient gameFeignClient;

    @Autowired
    private ObjectMapper objectMapper;

    private Integer groupId;
    private final Integer leagueId = 10;
    private final Integer gameId = 5;

    @BeforeEach
    void setUp() {
        Mockito.reset(jpaGroupRepository);
        Mockito.reset(jpaGroupMemberRepository);
        Mockito.reset(jpaPredictionRepository);

        AppUserDetails principal = new AppUserDetails(
                AppUser.builder()
                        .id(USER_ID)
                        .username(USERNAME)
                        .role(UserRole.USER)
                        .build()
        );
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        GroupEntity group = GroupEntity.builder()
                .name("Group A")
                .ownerId(USER_ID)
                .leagueId(leagueId)
                .inviteCode("CODE1234")
                .createdAt(LocalDateTime.now())
                .build();
        group = jpaGroupRepository.save(group);
        groupId = group.getId();

        GroupMemberEntity member = GroupMemberEntity.builder()
                .groupId(groupId)
                .userId(USER_ID)
                .alias(USERNAME)
                .totalPoints(15)
                .build();
        jpaGroupMemberRepository.save(member);

        PredictionEntity prediction = PredictionEntity.builder()
                .groupId(groupId)
                .userId(USER_ID)
                .gameId(gameId)
                .predictedHomeScore(2)
                .predictedAwayScore(1)
                .pointsEarned(3)
                .status(PredictionStatus.EVALUATED)
                .build();
        jpaPredictionRepository.save(prediction);

        Mockito.reset(jpaGroupRepository);
        Mockito.reset(jpaGroupMemberRepository);
        Mockito.reset(jpaPredictionRepository);
    }

    @Test
    void shouldCreateGroupSuccessfully() throws Exception {
        WebGroupCreateRequest createRequest = WebGroupCreateRequest.builder()
                .name("New Group")
                .leagueId(20)
                .build();

        mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("New Group")))
                .andExpect(jsonPath("$.ownerId", is(USER_ID)))
                .andExpect(jsonPath("$.leagueId", is(20)))
                .andExpect(jsonPath("$.inviteCode", notNullValue()));

        verify(jpaGroupRepository).save(any(GroupEntity.class));
        verify(jpaGroupMemberRepository).save(any(GroupMemberEntity.class));
    }

    @Test
    void shouldJoinGroupSuccessfully() throws Exception {
        GroupEntity otherGroup = GroupEntity.builder()
                .name("Other Group")
                .ownerId(99)
                .leagueId(10)
                .inviteCode("CODE5678")
                .createdAt(LocalDateTime.now())
                .build();
        otherGroup = jpaGroupRepository.save(otherGroup);
        Integer otherGroupId = otherGroup.getId();

        Mockito.reset(jpaGroupRepository);
        Mockito.reset(jpaGroupMemberRepository);

        WebGroupJoinRequest joinRequest = WebGroupJoinRequest.builder()
                .inviteCode("CODE5678")
                .build();

        mockMvc.perform(post("/groups/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(otherGroupId)))
                .andExpect(jsonPath("$.name", is("Other Group")));

        verify(jpaGroupRepository).findByInviteCode("CODE5678");
        verify(jpaGroupMemberRepository).findByGroupId(otherGroupId);
        verify(jpaGroupMemberRepository).save(any(GroupMemberEntity.class));
    }

    @Test
    void shouldGetGroupLeaderboardSuccessfully() throws Exception {
        mockMvc.perform(get("/groups/{groupId}/leaderboard", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupId", is(groupId)))
                .andExpect(jsonPath("$.groupName", is("Group A")))
                .andExpect(jsonPath("$.members", hasSize(1)))
                .andExpect(jsonPath("$.members[0].userId", is(USER_ID)))
                .andExpect(jsonPath("$.members[0].alias", is(USERNAME)))
                .andExpect(jsonPath("$.members[0].totalPoints", is(15)))
                .andExpect(jsonPath("$.members[0].rank", is(1)));

        verify(jpaGroupRepository).findById(groupId);
        verify(jpaGroupMemberRepository).findByGroupId(groupId);
    }

    @Test
    void shouldGetGroupGamePredictionsSuccessfully() throws Exception {
         WebGameResponse mockGame = WebGameResponse.builder()
                .id(gameId)
                .leagueId(leagueId)
                .homeTeamId(101)
                .awayTeamId(102)
                .homeScore(2)
                .awayScore(1)
                .date(LocalDate.now().minusDays(1))
                .matchday(1)
                .build();
        when(gameFeignClient.getGameById(gameId)).thenReturn(mockGame);

        mockMvc.perform(get("/groups/{groupId}/games/{gameId}/predictions", groupId, gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(USER_ID)))
                .andExpect(jsonPath("$[0].alias", is(USERNAME)))
                .andExpect(jsonPath("$[0].predictedHomeScore", is(2)))
                .andExpect(jsonPath("$[0].predictedAwayScore", is(1)))
                .andExpect(jsonPath("$[0].pointsEarned", is(3)));

        verify(jpaGroupMemberRepository).findByGroupId(groupId);
        verify(gameFeignClient).getGameById(gameId);
        verify(jpaPredictionRepository).findByGroupIdAndGameId(groupId, gameId);
    }

    @Test
    void shouldGetGroupMatchdayPredictionsSuccessfully() throws Exception {
        WebGameResponse mockGame = WebGameResponse.builder()
                .id(gameId)
                .leagueId(leagueId)
                .homeTeamId(101)
                .awayTeamId(102)
                .homeScore(2)
                .awayScore(1)
                .date(LocalDate.now().minusDays(1))
                .matchday(1)
                .build();
        when(gameFeignClient.getGamesByLeagueAndMatchday(eq(leagueId), eq(1)))
                .thenReturn(List.of(mockGame));

        mockMvc.perform(get("/groups/{groupId}/matchdays/{matchday}/predictions", groupId, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].gameId", is(gameId)))
                .andExpect(jsonPath("$[0].userId", is(USER_ID)))
                .andExpect(jsonPath("$[0].alias", is(USERNAME)))
                .andExpect(jsonPath("$[0].predictedHomeScore", is(2)))
                .andExpect(jsonPath("$[0].predictedAwayScore", is(1)))
                .andExpect(jsonPath("$[0].pointsEarned", is(3)));

        verify(jpaGroupMemberRepository).findByGroupId(groupId);
        verify(jpaGroupRepository).findById(groupId);
        verify(gameFeignClient).getGamesByLeagueAndMatchday(eq(leagueId), eq(1));
        verify(jpaPredictionRepository).findByGroupIdAndGameIdIn(eq(groupId), any());
    }
}
