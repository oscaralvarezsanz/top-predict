package com.topleague.predict.infrastructure.in.rest.controller;

import com.topleague.predict.application.port.in.prediction.SubmitPredictionUseCase;
import com.topleague.predict.domain.model.Prediction;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.rest.mapper.PredictionWebConverter;
import com.topleague.predict.infrastructure.in.model.WebPredictionResponse;
import com.topleague.predict.infrastructure.in.model.WebPredictionSubmitRequest;
import com.topleague.predict.infrastructure.in.controller.PredictionsApi;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PredictionController implements PredictionsApi {

    private final SubmitPredictionUseCase submitPredictionUseCase;
    private final PredictionWebConverter predictionWebConverter;

    public PredictionController(SubmitPredictionUseCase submitPredictionUseCase,
                                PredictionWebConverter predictionWebConverter) {
        this.submitPredictionUseCase = submitPredictionUseCase;
        this.predictionWebConverter = predictionWebConverter;
    }

    @Override
    public ResponseEntity<WebPredictionResponse> submitPrediction(WebPredictionSubmitRequest webPredictionSubmitRequest) {
        AppUserDetails principal = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Prediction predictionToSubmit = predictionWebConverter.toDomain(webPredictionSubmitRequest).toBuilder()
                .userId(principal.getId())
                .build();

        Prediction submittedPrediction = submitPredictionUseCase.submitPrediction(predictionToSubmit);

        return ResponseEntity.ok(predictionWebConverter.toWebResponse(submittedPrediction));
    }
}
