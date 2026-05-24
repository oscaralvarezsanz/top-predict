package com.topleague.predict.infrastructure.out.db.groupmember;

import com.topleague.predict.application.port.out.groupmember.GroupMemberCreateRepository;
import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.infrastructure.out.db.groupmember.mapper.GroupMemberEntityConverter;
import com.topleague.predict.infrastructure.out.db.model.GroupMemberEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MySqlGroupMemberCreateRepository implements GroupMemberCreateRepository {

    private final JpaGroupMemberRepository jpaRepository;
    private final GroupMemberEntityConverter entityConverter;

    public MySqlGroupMemberCreateRepository(JpaGroupMemberRepository jpaRepository,
                                             GroupMemberEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public GroupMember createGroupMember(GroupMember groupMember) {
        GroupMemberEntity memberToSave = entityConverter.toEntity(groupMember);
        GroupMemberEntity savedMember = jpaRepository.save(memberToSave);
        return entityConverter.toDomain(savedMember);
    }
}
