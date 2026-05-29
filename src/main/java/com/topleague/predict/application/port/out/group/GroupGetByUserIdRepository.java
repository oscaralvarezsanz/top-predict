package com.topleague.predict.application.port.out.group;

import com.topleague.predict.domain.model.Group;

import java.util.List;

public interface GroupGetByUserIdRepository {
    List<Group> getGroupsByUserId(Integer userId);
}
