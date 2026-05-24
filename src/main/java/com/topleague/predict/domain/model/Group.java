package com.topleague.predict.domain.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class Group {
    private final Integer id;
    private final String name;
    private final Integer ownerId;
    private final Integer leagueId;
    private final String inviteCode;
    private final LocalDateTime createdAt;
}
