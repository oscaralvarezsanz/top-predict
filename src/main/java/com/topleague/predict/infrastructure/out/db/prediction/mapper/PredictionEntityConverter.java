package com.topleague.predict.infrastructure.out.db.prediction.mapper;

import com.topleague.predict.domain.model.Prediction;
import com.topleague.predict.infrastructure.out.db.model.PredictionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PredictionEntityConverter {
    Prediction toDomain(PredictionEntity entity);
    PredictionEntity toEntity(Prediction domain);
}
