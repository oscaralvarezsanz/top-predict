package com.topleague.predict.infrastructure.in.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topleague.predict.domain.model.AppUser;
import com.topleague.predict.domain.model.UserRole;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.model.WebPredictionSubmitRequest;
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class PredictionE2EIT {

    private static final Integer USER_ID = 42;
    private static final String USERNAME = "john_doe";

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private JpaPredictionRepository jpaPredictionRepository;

    @Autowired
    private JpaGroupRepository jpaGroupRepository;

    @Autowired
    private JpaGroupMemberRepository jpaGroupMemberRepository;

    @MockitoBean
    private GameFeignClient gameFeignClient;

    @Autowired
    private ObjectMapper objectMapper;

    private Integer groupId;
    private final Integer gameId = 5;

    @BeforeEach
    void setUp() {
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
                .totalPoints(0)
                .build();
        jpaGroupMemberRepository.save(member);

        WebGameResponse mockGame = WebGameResponse.builder()
                .id(gameId)
                .leagueId(10)
                .homeTeamId(101)
                .awayTeamId(102)
                .date(LocalDate.now().plusDays(2))
                .matchday(1)
                .build();
        when(gameFeignClient.getGameById(gameId)).thenReturn(mockGame);

        Mockito.reset(jpaPredictionRepository);
    }

    @Test
    void shouldSubmitPredictionSuccessfully() throws Exception {
        WebPredictionSubmitRequest submitRequest = WebPredictionSubmitRequest.builder()
                .groupId(groupId)
                .gameId(gameId)
                .predictedHomeScore(2)
                .predictedAwayScore(1)
                .build();

        mockMvc.perform(post("/predictions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.groupId", is(groupId)))
                .andExpect(jsonPath("$.gameId", is(gameId)))
                .andExpect(jsonPath("$.predictedHomeScore", is(2)))
                .andExpect(jsonPath("$.predictedAwayScore", is(1)))
                .andExpect(jsonPath("$.status", is("PENDING")));

        verify(jpaPredictionRepository).save(any(PredictionEntity.class));
    }
}
