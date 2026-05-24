package com.topleague.predict.application.port.out.group;

import com.topleague.predict.domain.model.Group;

public interface GroupCreateRepository {
    Group createGroup(Group group);
    boolean existsByInviteCode(String inviteCode);
}
