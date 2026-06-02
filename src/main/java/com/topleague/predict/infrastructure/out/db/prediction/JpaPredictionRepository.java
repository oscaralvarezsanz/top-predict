package com.topleague.predict.infrastructure.out.db.prediction;

import com.topleague.predict.infrastructure.out.db.model.PredictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPredictionRepository extends JpaRepository<PredictionEntity, Integer> {
    Optional<PredictionEntity> findByGroupIdAndUserIdAndGameId(Integer groupId, Integer userId, Integer gameId);
    List<PredictionEntity> findByGroupIdAndUserId(Integer groupId, Integer userId);
}
