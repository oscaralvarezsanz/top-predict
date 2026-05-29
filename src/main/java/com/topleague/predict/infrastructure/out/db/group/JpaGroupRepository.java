package com.topleague.predict.infrastructure.out.db.group;

import com.topleague.predict.infrastructure.out.db.model.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaGroupRepository extends JpaRepository<GroupEntity, Integer> {
    Optional<GroupEntity> findByInviteCode(String inviteCode);
    boolean existsByInviteCode(String inviteCode);

    @Query("SELECT g FROM GroupEntity g WHERE g.id IN (SELECT gm.groupId FROM GroupMemberEntity gm WHERE gm.userId = :userId)")
    List<GroupEntity> findByUserId(@Param("userId") Integer userId);
}
