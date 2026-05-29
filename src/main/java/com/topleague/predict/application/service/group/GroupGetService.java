package com.topleague.predict.application.service.group;

import com.topleague.predict.application.port.in.group.GetMyGroupsUseCase;
import com.topleague.predict.application.port.out.group.GroupGetByUserIdRepository;
import com.topleague.predict.domain.model.Group;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupGetService implements GetMyGroupsUseCase {

    private final GroupGetByUserIdRepository groupGetByUserIdRepository;

    public GroupGetService(GroupGetByUserIdRepository groupGetByUserIdRepository) {
        this.groupGetByUserIdRepository = groupGetByUserIdRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> getMyGroups(Integer userId) {
        return groupGetByUserIdRepository.getGroupsByUserId(userId);
    }
}
