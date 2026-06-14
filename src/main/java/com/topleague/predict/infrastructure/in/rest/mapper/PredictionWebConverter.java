package com.topleague.predict.infrastructure.in.rest.mapper;

import com.topleague.predict.domain.model.Prediction;
import com.topleague.predict.infrastructure.in.model.WebPredictionResponse;
import com.topleague.predict.infrastructure.in.model.WebPredictionSubmitRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PredictionWebConverter {
    Prediction toDomain(WebPredictionSubmitRequest request);
    WebPredictionResponse toWebResponse(Prediction prediction);
}
