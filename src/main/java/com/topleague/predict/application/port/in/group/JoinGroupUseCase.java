package com.topleague.predict.application.port.in.group;

import com.topleague.predict.domain.model.Group;

public interface JoinGroupUseCase {
    Group joinGroup(String inviteCode, Integer userId, String alias);
}
