package com.topleague.predict.application.port.in.group;

import com.topleague.predict.domain.model.Group;

import java.util.List;

public interface GetMyGroupsUseCase {
    List<Group> getMyGroups(Integer userId);
}
