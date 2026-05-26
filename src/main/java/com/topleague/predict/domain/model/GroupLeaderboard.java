package com.topleague.predict.domain.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class GroupLeaderboard {
    private final Integer groupId;
    private final String groupName;
    private final List<GroupMember> members;
}
