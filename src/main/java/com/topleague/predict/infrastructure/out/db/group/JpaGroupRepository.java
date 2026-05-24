package com.topleague.predict.infrastructure.out.db.group;

import com.topleague.predict.infrastructure.out.db.model.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaGroupRepository extends JpaRepository<GroupEntity, Integer> {
    Optional<GroupEntity> findByInviteCode(String inviteCode);
    boolean existsByInviteCode(String inviteCode);
}
