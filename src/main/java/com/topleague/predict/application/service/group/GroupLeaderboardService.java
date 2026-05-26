package com.topleague.predict.application.service.group;

import com.topleague.predict.application.port.in.group.GetGroupLeaderboardUseCase;
import com.topleague.predict.application.port.out.group.GroupGetByIdRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.domain.exception.GroupErrorCode;
import com.topleague.predict.domain.exception.GroupException;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.GroupMemberException;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupLeaderboard;
import com.topleague.predict.domain.model.GroupMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupLeaderboardService implements GetGroupLeaderboardUseCase {

    private final GroupGetByIdRepository groupByIdRepository;
    private final GroupMemberGetByGroupIdRepository groupMembersByGroupIdRepository;

    public GroupLeaderboardService(GroupGetByIdRepository groupByIdRepository,
                                   GroupMemberGetByGroupIdRepository groupMembersByGroupIdRepository) {
        this.groupByIdRepository = groupByIdRepository;
        this.groupMembersByGroupIdRepository = groupMembersByGroupIdRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public GroupLeaderboard getGroupLeaderboard(Integer groupId, Integer userId) {
        Group group = groupByIdRepository.getGroupById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        List<GroupMember> members = groupMembersByGroupIdRepository.getGroupMembersByGroupId(groupId);

        boolean isMember = members.stream()
                .anyMatch(member -> member.getUserId().equals(userId));

        if (!isMember) {
            throw new GroupMemberException(GroupMemberErrorCode.MEMBER_NOT_FOUND);
        }

        List<GroupMember> rankedMembers = getRankedMembers(members);

        return GroupLeaderboard.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .members(rankedMembers)
                .build();
    }

    private List<GroupMember> getRankedMembers(List<GroupMember> members) {
        List<GroupMember> sortedMembers = members.stream()
                .sorted(Comparator.comparing(GroupMember::getTotalPoints).reversed())
                .collect(Collectors.toList());

        List<GroupMember> rankedMembers = new ArrayList<>();
        int currentRank = 1;
        int currentPoints = -1;
        int position = 0;

        for (GroupMember member : sortedMembers) {
            position++;
            if (member.getTotalPoints() != currentPoints) {
                currentRank = position;
                currentPoints = member.getTotalPoints();
            }
            rankedMembers.add(member.toBuilder().rank(currentRank).build());
        }
        return rankedMembers;
    }
}
