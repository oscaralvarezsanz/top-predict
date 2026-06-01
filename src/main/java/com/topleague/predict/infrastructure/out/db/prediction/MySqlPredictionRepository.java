package com.topleague.predict.infrastructure.out.db.prediction;

import com.topleague.predict.application.port.out.prediction.PredictionGetByGroupUserAndGameRepository;
import com.topleague.predict.application.port.out.prediction.PredictionSaveRepository;
import com.topleague.predict.domain.model.Prediction;
import com.topleague.predict.infrastructure.out.db.model.PredictionEntity;
import com.topleague.predict.infrastructure.out.db.prediction.mapper.PredictionEntityConverter;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MySqlPredictionRepository implements PredictionSaveRepository, PredictionGetByGroupUserAndGameRepository {

    private final JpaPredictionRepository jpaRepository;
    private final PredictionEntityConverter entityConverter;

    public MySqlPredictionRepository(JpaPredictionRepository jpaRepository,
                                     PredictionEntityConverter entityConverter) {
        this.jpaRepository = jpaRepository;
        this.entityConverter = entityConverter;
    }

    @Override
    public Prediction savePrediction(Prediction prediction) {
        PredictionEntity entity = entityConverter.toEntity(prediction);
        PredictionEntity savedEntity = jpaRepository.save(entity);
        return entityConverter.toDomain(savedEntity);
    }

    @Override
    public Optional<Prediction> getPredictionByGroupUserAndGame(Integer groupId, Integer userId, Integer gameId) {
        return jpaRepository.findByGroupIdAndUserIdAndGameId(groupId, userId, gameId)
                .map(entityConverter::toDomain);
    }
}
