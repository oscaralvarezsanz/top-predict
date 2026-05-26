package com.topleague.predict.infrastructure.in.mapper;

import com.topleague.predict.domain.model.GroupLeaderboard;
import com.topleague.predict.infrastructure.in.model.WebLeaderboardResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {GroupMemberWebConverter.class}, unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface LeaderboardWebConverter {
    WebLeaderboardResponse toWebResponse(GroupLeaderboard leaderboard);
}
