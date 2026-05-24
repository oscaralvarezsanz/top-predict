package com.topleague.predict.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class GroupMember {
    private final Integer id;
    private final Integer groupId;
    private final Integer userId;
    private final String alias;
    @Builder.Default
    private final Integer totalPoints = 0;
}
