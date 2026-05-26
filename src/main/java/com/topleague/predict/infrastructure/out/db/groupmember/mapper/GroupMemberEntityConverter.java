package com.topleague.predict.infrastructure.out.db.groupmember.mapper;

import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.infrastructure.out.db.model.GroupMemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMemberEntityConverter {
    @Mapping(target = "rank", ignore = true)
    GroupMember toDomain(GroupMemberEntity entity);
    GroupMemberEntity toEntity(GroupMember domain);
    List<GroupMember> toDomainList(List<GroupMemberEntity> entities);
}
