package com.topleague.predict.application.port.in.group;

import com.topleague.predict.domain.model.Group;

public interface CreateGroupUseCase {
    Group createGroup(Group groupToCreate, String alias);
}
