package com.topleague.predict.infrastructure.in.rest.controller;

import com.topleague.predict.domain.model.AppUser;
import com.topleague.predict.domain.model.UserRole;
import com.topleague.predict.domain.model.PredictionStatus;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.out.db.group.JpaGroupRepository;
import com.topleague.predict.infrastructure.out.db.groupmember.JpaGroupMemberRepository;
import com.topleague.predict.infrastructure.out.db.model.GroupEntity;
import com.topleague.predict.infrastructure.out.db.model.GroupMemberEntity;
import com.topleague.predict.infrastructure.out.db.model.PredictionEntity;
import com.topleague.predict.infrastructure.out.db.prediction.JpaPredictionRepository;
import com.topleague.predict.infrastructure.out.rest.game.GameFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class MeE2EIT {

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

    private Integer groupId;

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
                .leagueId(10)
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
                .gameId(5)
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
    void shouldGetMyGroups() throws Exception {
        mockMvc.perform(get("/me/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(groupId)))
                .andExpect(jsonPath("$[0].name", is("Group A")));

        verify(jpaGroupRepository).findByUserId(USER_ID);
    }

    @Test
    void shouldGetMyGroupPredictions() throws Exception {
        mockMvc.perform(get("/me/groups/{groupId}/predictions", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].groupId", is(groupId)))
                .andExpect(jsonPath("$[0].userId", is(USER_ID)))
                .andExpect(jsonPath("$[0].gameId", is(5)))
                .andExpect(jsonPath("$[0].predictedHomeScore", is(2)))
                .andExpect(jsonPath("$[0].predictedAwayScore", is(1)))
                .andExpect(jsonPath("$[0].pointsEarned", is(3)))
                .andExpect(jsonPath("$[0].status", is("EVALUATED")));

        verify(jpaGroupMemberRepository).findByGroupId(groupId);
        verify(jpaPredictionRepository).findByGroupIdAndUserId(groupId, USER_ID);
    }
}
