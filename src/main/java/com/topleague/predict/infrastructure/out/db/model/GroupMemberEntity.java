package com.topleague.predict.infrastructure.out.db.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_member",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"group_id", "user_id"})
       })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "group_id", nullable = false)
    private Integer groupId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "alias", nullable = false, length = 50)
    private String alias;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;
}
