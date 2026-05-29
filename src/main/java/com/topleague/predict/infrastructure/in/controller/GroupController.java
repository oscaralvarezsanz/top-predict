package com.topleague.predict.infrastructure.in.controller;

import com.topleague.predict.application.port.in.group.CreateGroupUseCase;
import com.topleague.predict.application.port.in.group.GetGroupLeaderboardUseCase;
import com.topleague.predict.application.port.in.group.JoinGroupUseCase;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupLeaderboard;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.mapper.GroupWebConverter;
import com.topleague.predict.infrastructure.in.mapper.LeaderboardWebConverter;
import com.topleague.predict.infrastructure.in.model.WebGroupCreateRequest;
import com.topleague.predict.infrastructure.in.model.WebGroupJoinRequest;
import com.topleague.predict.infrastructure.in.model.WebGroupResponse;
import com.topleague.predict.infrastructure.in.model.WebLeaderboardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupController implements GroupsApi {

    private final CreateGroupUseCase createGroupUseCase;
    private final GetGroupLeaderboardUseCase getGroupLeaderboardUseCase;
    private final JoinGroupUseCase joinGroupUseCase;
    private final GroupWebConverter groupWebConverter;
    private final LeaderboardWebConverter leaderboardWebConverter;

    public GroupController(CreateGroupUseCase createGroupUseCase,
                           GetGroupLeaderboardUseCase getGroupLeaderboardUseCase,
                           JoinGroupUseCase joinGroupUseCase,
                           GroupWebConverter groupWebConverter,
                           LeaderboardWebConverter leaderboardWebConverter) {
        this.createGroupUseCase = createGroupUseCase;
        this.getGroupLeaderboardUseCase = getGroupLeaderboardUseCase;
        this.joinGroupUseCase = joinGroupUseCase;
        this.groupWebConverter = groupWebConverter;
        this.leaderboardWebConverter = leaderboardWebConverter;
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

    @Override
    public ResponseEntity<WebLeaderboardResponse> getGroupLeaderboard(Integer groupId) {
        AppUserDetails principal = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        GroupLeaderboard leaderboard = getGroupLeaderboardUseCase.getGroupLeaderboard(groupId, principal.getId());

        return ResponseEntity.ok(leaderboardWebConverter.toWebResponse(leaderboard));
    }

    @Override
    public ResponseEntity<WebGroupResponse> joinGroup(WebGroupJoinRequest webGroupJoinRequest) {
        AppUserDetails principal = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Group joinedGroup = joinGroupUseCase.joinGroup(
                webGroupJoinRequest.getInviteCode(),
                principal.getId(),
                principal.getUsername()
        );

        return ResponseEntity.ok(groupWebConverter.toWebResponse(joinedGroup));
    }
}
