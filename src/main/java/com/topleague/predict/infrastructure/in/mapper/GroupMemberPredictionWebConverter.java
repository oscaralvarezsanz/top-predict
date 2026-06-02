package com.topleague.predict.infrastructure.in.mapper;

import com.topleague.predict.domain.model.GroupMemberPrediction;
import com.topleague.predict.infrastructure.in.model.WebGroupMemberPrediction;
import org.mapstruct.Mapper;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface GroupMemberPredictionWebConverter {

    WebGroupMemberPrediction toWebResponse(GroupMemberPrediction prediction);

    default <T> JsonNullable<T> map(T value) {
        return value == null ? JsonNullable.undefined() : JsonNullable.of(value);
    }

    default <T> T map(JsonNullable<T> jsonNullable) {
        return jsonNullable == null || !jsonNullable.isPresent() ? null : jsonNullable.get();
    }
}
