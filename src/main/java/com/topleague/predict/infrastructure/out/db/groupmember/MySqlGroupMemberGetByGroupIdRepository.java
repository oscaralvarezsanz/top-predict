package com.topleague.predict.infrastructure.out.db.groupmember;

import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.infrastructure.out.db.groupmember.mapper.GroupMemberEntityConverter;
import com.topleague.predict.infrastructure.out.db.model.GroupMemberEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MySqlGroupMemberGetByGroupIdRepository implements GroupMemberGetByGroupIdRepository {

    private final JpaGroupMemberRepository jpaRepository;
    private final GroupMemberEntityConverter entityConverter;

    public MySqlGroupMemberGetByGroupIdRepository(JpaGroupMemberRepository jpaRepository,
                                                 GroupMemberEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public List<GroupMember> getGroupMembersByGroupId(Integer groupId) {
        List<GroupMemberEntity> groupMembers = jpaRepository.findByGroupId(groupId);
        return entityConverter.toDomainList(groupMembers);
    }
}
