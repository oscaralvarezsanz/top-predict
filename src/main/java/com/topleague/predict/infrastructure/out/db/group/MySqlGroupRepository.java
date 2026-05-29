package com.topleague.predict.infrastructure.out.db.group;

import com.topleague.predict.application.port.out.group.GroupCreateRepository;
import com.topleague.predict.application.port.out.group.GroupGetByIdRepository;
import com.topleague.predict.application.port.out.group.GroupGetByInviteCodeRepository;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.out.db.group.mapper.GroupEntityConverter;
import com.topleague.predict.infrastructure.out.db.model.GroupEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MySqlGroupRepository implements GroupCreateRepository, GroupGetByIdRepository, GroupGetByInviteCodeRepository {

    private final JpaGroupRepository jpaRepository;
    private final GroupEntityConverter entityConverter;

    public MySqlGroupRepository(JpaGroupRepository jpaRepository,
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

    @Override
    public Optional<Group> getGroupById(Integer id) {
        return jpaRepository.findById(id)
                .map(entityConverter::toDomain);
    }

    @Override
    public Optional<Group> getGroupByInviteCode(String inviteCode) {
        return jpaRepository.findByInviteCode(inviteCode)
                .map(entityConverter::toDomain);
    }
}
