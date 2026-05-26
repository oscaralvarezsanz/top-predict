package com.topleague.predict.application.port.out.group;

import com.topleague.predict.domain.model.Group;
import java.util.Optional;

public interface GroupGetByIdRepository {
    Optional<Group> getGroupById(Integer id);
}
