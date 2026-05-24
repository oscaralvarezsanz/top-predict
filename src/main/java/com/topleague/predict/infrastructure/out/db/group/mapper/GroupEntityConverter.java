package com.topleague.predict.infrastructure.out.db.group.mapper;

import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.out.db.model.GroupEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupEntityConverter {
    Group toDomain(GroupEntity entity);
    GroupEntity toEntity(Group domain);
}
