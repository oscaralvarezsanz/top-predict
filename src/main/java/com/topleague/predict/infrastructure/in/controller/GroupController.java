package com.topleague.predict.infrastructure.in.controller;

import com.topleague.predict.application.port.in.group.CreateGroupUseCase;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.mapper.GroupWebConverter;
import com.topleague.predict.infrastructure.in.model.WebGroupCreateRequest;
import com.topleague.predict.infrastructure.in.model.WebGroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupController implements GroupsApi {

    private final CreateGroupUseCase createGroupUseCase;
    private final GroupWebConverter groupWebConverter;

    public GroupController(CreateGroupUseCase createGroupUseCase, GroupWebConverter groupWebConverter) {
        this.createGroupUseCase = createGroupUseCase;
        this.groupWebConverter = groupWebConverter;
    }

    @Override
    public ResponseEntity<WebGroupResponse> createGroup(WebGroupCreateRequest webGroupCreateRequest) {
        AppUserDetails principal = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Group groupToCreate = groupWebConverter.toDomain(webGroupCreateRequest).toBuilder()
                .ownerId(principal.getId())
                .build();

        Group createdGroup = createGroupUseCase.createGroup(groupToCreate, principal.getUsername());

        return ResponseEntity.status(201).body(groupWebConverter.toWebResponse(createdGroup));
    }
}
