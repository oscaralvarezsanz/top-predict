package com.topleague.predict.infrastructure.in.rest.mapper;

import com.topleague.predict.domain.model.GroupMemberPrediction;
import com.topleague.predict.infrastructure.in.model.WebGroupMemberPrediction;
import com.topleague.predict.infrastructure.in.model.WebMatchdayPredictionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GroupMemberPredictionWebConverter {

    WebGroupMemberPrediction toWebResponse(GroupMemberPrediction prediction);

    WebMatchdayPredictionResponse toWebMatchdayResponse(GroupMemberPrediction prediction);
}
