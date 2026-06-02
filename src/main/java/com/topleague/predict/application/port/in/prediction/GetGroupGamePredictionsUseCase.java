package com.topleague.predict.application.port.in.prediction;

import com.topleague.predict.domain.model.GroupMemberPrediction;

import java.util.List;

public interface GetGroupGamePredictionsUseCase {
    List<GroupMemberPrediction> getGroupGamePredictions(Integer groupId, Integer gameId, Integer userId);
}
