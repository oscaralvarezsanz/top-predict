package com.topleague.predict.infrastructure.in.controller;

import com.topleague.predict.application.port.in.group.GetMyGroupsUseCase;
import com.topleague.predict.application.port.in.prediction.GetMyGroupPredictionsUseCase;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.Prediction;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.mapper.GroupWebConverter;
import com.topleague.predict.infrastructure.in.mapper.PredictionWebConverter;
import com.topleague.predict.infrastructure.in.model.WebGroupResponse;
import com.topleague.predict.infrastructure.in.model.WebPredictionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MeController implements MeApi {

    private final GetMyGroupsUseCase getMyGroupsUseCase;
    private final GetMyGroupPredictionsUseCase getMyGroupPredictionsUseCase;
    private final GroupWebConverter groupWebConverter;
    private final PredictionWebConverter predictionWebConverter;

    public MeController(GetMyGroupsUseCase getMyGroupsUseCase,
                        GetMyGroupPredictionsUseCase getMyGroupPredictionsUseCase,
                        GroupWebConverter groupWebConverter,
                        PredictionWebConverter predictionWebConverter) {
        this.getMyGroupsUseCase = getMyGroupsUseCase;
        this.getMyGroupPredictionsUseCase = getMyGroupPredictionsUseCase;
        this.groupWebConverter = groupWebConverter;
        this.predictionWebConverter = predictionWebConverter;
    }

    @Override
    public ResponseEntity<List<WebGroupResponse>> getMyGroups() {
        AppUserDetails principal = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Group> myGroups = getMyGroupsUseCase.getMyGroups(principal.getId());

        List<WebGroupResponse> response = myGroups.stream()
                .map(groupWebConverter::toWebResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<WebPredictionResponse>> getMyGroupPredictions(Integer groupId) {
        AppUserDetails principal = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Prediction> myPredictions = getMyGroupPredictionsUseCase
                .getMyGroupPredictions(groupId, principal.getId());

        List<WebPredictionResponse> response = myPredictions.stream()
                .map(predictionWebConverter::toWebResponse)
                .toList();

        return ResponseEntity.ok(response);
    }
}
