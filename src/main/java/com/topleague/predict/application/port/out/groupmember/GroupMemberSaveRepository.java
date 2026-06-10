package com.topleague.predict.application.port.out.groupmember;

import com.topleague.predict.domain.model.GroupMember;

public interface GroupMemberSaveRepository {
    GroupMember saveGroupMember(GroupMember groupMember);
}
