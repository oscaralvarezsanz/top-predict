package com.topleague.predict.infrastructure.out.db.groupmember;

import com.topleague.predict.infrastructure.out.db.model.GroupMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaGroupMemberRepository extends JpaRepository<GroupMemberEntity, Integer> {
    List<GroupMemberEntity> findByGroupId(Integer groupId);
    Optional<GroupMemberEntity> findByGroupIdAndUserId(Integer groupId, Integer userId);
}
