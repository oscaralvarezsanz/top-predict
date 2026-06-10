package com.topleague.predict.infrastructure.in.rest.mapper;

import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.in.model.WebGroupCreateRequest;
import com.topleague.predict.infrastructure.in.model.WebGroupResponse;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GroupWebConverter {
    Group toDomain(WebGroupCreateRequest request);
    WebGroupResponse toWebResponse(Group group);
}
