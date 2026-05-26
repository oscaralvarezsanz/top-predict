package com.topleague.predict.application.port.in.group;

import com.topleague.predict.domain.model.GroupLeaderboard;

public interface GetGroupLeaderboardUseCase {
    GroupLeaderboard getGroupLeaderboard(Integer groupId, Integer userId);
}
