package com.topleague.predict.application.service.group;

import com.topleague.predict.application.port.in.group.JoinGroupUseCase;
import com.topleague.predict.application.port.out.group.GroupGetByInviteCodeRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberSaveRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.domain.exception.GroupErrorCode;
import com.topleague.predict.domain.exception.GroupException;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.GroupMemberException;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupJoinService implements JoinGroupUseCase {

    private final GroupGetByInviteCodeRepository groupGetByInviteCodeRepository;
    private final GroupMemberGetByGroupIdRepository groupMemberGetByGroupIdRepository;
    private final GroupMemberSaveRepository groupMemberSaveRepository;

    public GroupJoinService(GroupGetByInviteCodeRepository groupGetByInviteCodeRepository,
                            GroupMemberGetByGroupIdRepository groupMemberGetByGroupIdRepository,
                            GroupMemberSaveRepository groupMemberSaveRepository) {
        this.groupGetByInviteCodeRepository = groupGetByInviteCodeRepository;
        this.groupMemberGetByGroupIdRepository = groupMemberGetByGroupIdRepository;
        this.groupMemberSaveRepository = groupMemberSaveRepository;
    }

    @Override
    @Transactional
    public Group joinGroup(String inviteCode, Integer userId, String alias) {
        Group group = groupGetByInviteCodeRepository.getGroupByInviteCode(inviteCode)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        if (isAlreadyMember(userId, group)) {
            throw new GroupMemberException(GroupMemberErrorCode.MEMBER_EXISTS);
        }

        GroupMember newMember = GroupMember.builder()
                .groupId(group.getId())
                .userId(userId)
                .alias(alias)
                .totalPoints(0)
                .build();

        groupMemberSaveRepository.saveGroupMember(newMember);

        return group;
    }

    private boolean isAlreadyMember(Integer userId, Group group) {
        return groupMemberGetByGroupIdRepository.getGroupMembersByGroupId(group.getId())
                .stream()
                .anyMatch(member -> member.getUserId().equals(userId));
    }
}
