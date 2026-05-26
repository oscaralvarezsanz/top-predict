package com.topleague.predict.infrastructure.in.mapper;

import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.infrastructure.in.model.WebGroupMemberStanding;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GroupMemberWebConverter {
    WebGroupMemberStanding toWebResponse(GroupMember member);
}
