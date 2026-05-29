package com.topleague.predict.infrastructure.in.controller;

import com.topleague.predict.application.port.in.group.GetMyGroupsUseCase;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.mapper.GroupWebConverter;
import com.topleague.predict.infrastructure.in.model.WebGroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MeController implements MeApi {

    private final GetMyGroupsUseCase getMyGroupsUseCase;
    private final GroupWebConverter groupWebConverter;

    public MeController(GetMyGroupsUseCase getMyGroupsUseCase,
                        GroupWebConverter groupWebConverter) {
        this.getMyGroupsUseCase = getMyGroupsUseCase;
        this.groupWebConverter = groupWebConverter;
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
}
