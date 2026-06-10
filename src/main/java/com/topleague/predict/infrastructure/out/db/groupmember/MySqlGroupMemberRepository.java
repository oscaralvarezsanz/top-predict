package com.topleague.predict.infrastructure.out.db.groupmember;

import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupAndUserRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberSaveRepository;
import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.infrastructure.out.db.groupmember.mapper.GroupMemberEntityConverter;
import com.topleague.predict.infrastructure.out.db.model.GroupMemberEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MySqlGroupMemberRepository implements GroupMemberGetByGroupIdRepository, GroupMemberGetByGroupAndUserRepository, GroupMemberSaveRepository {

    private final JpaGroupMemberRepository jpaRepository;
    private final GroupMemberEntityConverter entityConverter;

    public MySqlGroupMemberRepository(JpaGroupMemberRepository jpaRepository,
                                      GroupMemberEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public List<GroupMember> getGroupMembersByGroupId(Integer groupId) {
        List<GroupMemberEntity> groupMembers = jpaRepository.findByGroupId(groupId);
        return entityConverter.toDomainList(groupMembers);
    }

    @Override
    public Optional<GroupMember> getGroupMember(Integer groupId, Integer userId) {
        return jpaRepository.findByGroupIdAndUserId(groupId, userId)
                .map(entityConverter::toDomain);
    }

    @Override
    public GroupMember saveGroupMember(GroupMember groupMember) {
        GroupMemberEntity entity = entityConverter.toEntity(groupMember);
        GroupMemberEntity savedEntity = jpaRepository.save(entity);
        return entityConverter.toDomain(savedEntity);
    }
}
