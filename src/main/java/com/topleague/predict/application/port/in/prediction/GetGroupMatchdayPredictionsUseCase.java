package com.topleague.predict.application.port.in.prediction;

import com.topleague.predict.domain.model.GroupMemberPrediction;
import java.util.List;

public interface GetGroupMatchdayPredictionsUseCase {
    List<GroupMemberPrediction> getGroupMatchdayPredictions(Integer groupId, Integer matchday, Integer userId);
}
