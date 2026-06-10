package com.topleague.predict.application.port.out.groupmember;

import com.topleague.predict.domain.model.GroupMember;

import java.util.Optional;

public interface GroupMemberGetByGroupAndUserRepository {
    Optional<GroupMember> getGroupMember(Integer groupId, Integer userId);
}
