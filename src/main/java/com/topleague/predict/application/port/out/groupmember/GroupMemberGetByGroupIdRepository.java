package com.topleague.predict.application.port.out.groupmember;

import com.topleague.predict.domain.model.GroupMember;
import java.util.List;

public interface GroupMemberGetByGroupIdRepository {
    List<GroupMember> getGroupMembersByGroupId(Integer groupId);
}
