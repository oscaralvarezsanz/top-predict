package com.topleague.predict.infrastructure.out.db.groupmember.mapper;

import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.infrastructure.out.db.model.GroupMemberEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupMemberEntityConverter {
    GroupMember toDomain(GroupMemberEntity entity);
    GroupMemberEntity toEntity(GroupMember domain);
}
