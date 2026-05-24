package com.topleague.predict.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AppUser {
    private final Integer id;
    private final String username;
    private final String password;
    @Builder.Default
    private final UserRole role = UserRole.USER;
}
