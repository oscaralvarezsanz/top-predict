package com.topleague.predict.infrastructure.in.controller;

import com.topleague.predict.application.port.in.prediction.SubmitPredictionUseCase;
import com.topleague.predict.domain.model.AppUser;
import com.topleague.predict.domain.model.Prediction;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.mapper.PredictionWebConverter;
import com.topleague.predict.infrastructure.in.model.WebPredictionResponse;
import com.topleague.predict.infrastructure.in.model.WebPredictionSubmitRequest;
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

class PredictionControllerTest {

    private static final Integer USER_ID = 42;
    private static final String USERNAME = "john_doe";

    private final SubmitPredictionUseCase submitUseCase = mock(SubmitPredictionUseCase.class);
    private final PredictionWebConverter converter = mock(PredictionWebConverter.class);
    private final PredictionController controller = new PredictionController(submitUseCase, converter);

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
    void submitPredictionShouldInjectUserIdFromTokenAndReturn200Ok() {
        WebPredictionSubmitRequest webRequest = WebPredictionSubmitRequest.builder()
                .groupId(1)
                .gameId(5)
                .predictedHomeScore(2)
                .predictedAwayScore(1)
                .build();

        Prediction predictionFromConverter = Prediction.builder()
                .groupId(1)
                .gameId(5)
                .predictedHomeScore(2)
                .predictedAwayScore(1)
                .build();

        Prediction predictionWithUser = Prediction.builder()
                .groupId(1)
                .gameId(5)
                .userId(USER_ID)
                .predictedHomeScore(2)
                .predictedAwayScore(1)
                .build();

        Prediction submittedPrediction = Prediction.builder()
                .id(100)
                .groupId(1)
                .userId(USER_ID)
                .gameId(5)
                .predictedHomeScore(2)
                .predictedAwayScore(1)
                .build();

        WebPredictionResponse webResponse = WebPredictionResponse.builder()
                .id(100)
                .groupId(1)
                .userId(USER_ID)
                .gameId(5)
                .predictedHomeScore(2)
                .predictedAwayScore(1)
                .status(WebPredictionResponse.StatusEnum.PENDING)
                .build();

        when(converter.toDomain(webRequest)).thenReturn(predictionFromConverter);
        when(submitUseCase.submitPrediction(predictionWithUser)).thenReturn(submittedPrediction);
        when(converter.toWebResponse(submittedPrediction)).thenReturn(webResponse);

        ResponseEntity<WebPredictionResponse> response = controller.submitPrediction(webRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(webResponse));

        verify(converter).toDomain(webRequest);
        verify(submitUseCase).submitPrediction(predictionWithUser);
        verify(converter).toWebResponse(submittedPrediction);
    }
}
