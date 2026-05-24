package com.topleague.predict.infrastructure.out.db.group;

import com.topleague.predict.application.port.out.group.GroupCreateRepository;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.out.db.group.mapper.GroupEntityConverter;
import com.topleague.predict.infrastructure.out.db.model.GroupEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MySqlGroupCreateRepository implements GroupCreateRepository {

    private final JpaGroupRepository jpaRepository;
    private final GroupEntityConverter entityConverter;

    public MySqlGroupCreateRepository(JpaGroupRepository jpaRepository,
                                     GroupEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public Group createGroup(Group group) {
        GroupEntity groupToSave = entityConverter.toEntity(group);
        GroupEntity savedGroup = jpaRepository.save(groupToSave);
        return entityConverter.toDomain(savedGroup);
    }

    @Override
    public boolean existsByInviteCode(String inviteCode) {
        return jpaRepository.existsByInviteCode(inviteCode);
    }
}
