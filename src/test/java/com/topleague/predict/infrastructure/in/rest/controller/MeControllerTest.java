package com.topleague.predict.infrastructure.in.rest.controller;

import com.topleague.predict.application.port.in.group.GetMyGroupsUseCase;
import com.topleague.predict.application.port.in.prediction.GetMyGroupPredictionsUseCase;
import com.topleague.predict.domain.model.AppUser;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.Prediction;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.rest.controller.MeController;
import com.topleague.predict.infrastructure.in.rest.mapper.GroupWebConverter;
import com.topleague.predict.infrastructure.in.rest.mapper.PredictionWebConverter;
import com.topleague.predict.infrastructure.in.model.WebGroupResponse;
import com.topleague.predict.infrastructure.in.model.WebPredictionResponse;
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

class MeControllerTest {

    private static final Integer USER_ID = 42;
    private static final String USERNAME = "john_doe";

    private final GetMyGroupsUseCase getMyGroupsUseCase = mock(GetMyGroupsUseCase.class);
    private final GetMyGroupPredictionsUseCase getMyGroupPredictionsUseCase = mock(GetMyGroupPredictionsUseCase.class);
    private final GroupWebConverter groupWebConverter = mock(GroupWebConverter.class);
    private final PredictionWebConverter predictionWebConverter = mock(PredictionWebConverter.class);
    private final MeController controller = new MeController(getMyGroupsUseCase, getMyGroupPredictionsUseCase, groupWebConverter, predictionWebConverter);

    @BeforeEach
    void setup() {
        AppUserDetails principal = new AppUserDetails(AppUser.builder().id(USER_ID).username(USERNAME).build());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMyGroupsShouldReturnListWith200Ok() {
        Group group1 = Group.builder().id(1).name("Group 1").build();
        Group group2 = Group.builder().id(2).name("Group 2").build();
        List<Group> domainGroups = Arrays.asList(group1, group2);

        WebGroupResponse response1 = WebGroupResponse.builder().id(1).name("Group 1").build();
        WebGroupResponse response2 = WebGroupResponse.builder().id(2).name("Group 2").build();

        when(getMyGroupsUseCase.getMyGroups(USER_ID)).thenReturn(domainGroups);
        when(groupWebConverter.toWebResponse(group1)).thenReturn(response1);
        when(groupWebConverter.toWebResponse(group2)).thenReturn(response2);

        ResponseEntity<List<WebGroupResponse>> response = controller.getMyGroups();

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(Arrays.asList(response1, response2)));

        verify(getMyGroupsUseCase).getMyGroups(USER_ID);
        verify(groupWebConverter).toWebResponse(group1);
        verify(groupWebConverter).toWebResponse(group2);
    }

    @Test
    void getMyGroupPredictionsShouldReturnPredictionsWith200Ok() {
        Integer groupId = 10;
        Prediction pred1 = Prediction.builder().id(1).groupId(groupId).userId(USER_ID).predictedHomeScore(2).predictedAwayScore(1).build();
        Prediction pred2 = Prediction.builder().id(2).groupId(groupId).userId(USER_ID).predictedHomeScore(1).predictedAwayScore(1).build();
        List<Prediction> domainPredictions = Arrays.asList(pred1, pred2);

        WebPredictionResponse response1 = WebPredictionResponse.builder().id(1).groupId(groupId).userId(USER_ID).predictedHomeScore(2).predictedAwayScore(1).build();
        WebPredictionResponse response2 = WebPredictionResponse.builder().id(2).groupId(groupId).userId(USER_ID).predictedHomeScore(1).predictedAwayScore(1).build();

        when(getMyGroupPredictionsUseCase.getMyGroupPredictions(groupId, USER_ID)).thenReturn(domainPredictions);
        when(predictionWebConverter.toWebResponse(pred1)).thenReturn(response1);
        when(predictionWebConverter.toWebResponse(pred2)).thenReturn(response2);

        ResponseEntity<List<WebPredictionResponse>> response = controller.getMyGroupPredictions(groupId);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(Arrays.asList(response1, response2)));

        verify(getMyGroupPredictionsUseCase).getMyGroupPredictions(groupId, USER_ID);
        verify(predictionWebConverter).toWebResponse(pred1);
        verify(predictionWebConverter).toWebResponse(pred2);
    }
}
