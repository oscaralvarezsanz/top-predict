package com.topleague.predict.infrastructure.out.db.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_group")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "league_id", nullable = false)
    private Integer leagueId;

    @Column(name = "invite_code", nullable = false, length = 8, unique = true)
    private String inviteCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
