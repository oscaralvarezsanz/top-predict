package com.topleague.predict.infrastructure.out.db.groupmember;

import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.infrastructure.out.db.groupmember.mapper.GroupMemberEntityConverter;
import com.topleague.predict.infrastructure.out.db.model.GroupMemberEntity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class MySqlGroupMemberGetByGroupIdRepositoryTest {

    private final JpaGroupMemberRepository jpaRepository = mock(JpaGroupMemberRepository.class);
    private final GroupMemberEntityConverter converter = mock(GroupMemberEntityConverter.class);
    private final MySqlGroupMemberGetByGroupIdRepository sut = new MySqlGroupMemberGetByGroupIdRepository(jpaRepository, converter);

    @Test
    void getGroupMembersByGroupIdShouldDelegateToJpaRepositoryAndConvertList() {
        GroupMemberEntity entity = GroupMemberEntity.builder().id(1).groupId(10).alias("User").build();
        GroupMember domainMember = GroupMember.builder().id(1).groupId(10).alias("User").build();

        List<GroupMemberEntity> entities = Arrays.asList(entity);
        List<GroupMember> domainMembers = Arrays.asList(domainMember);

        when(jpaRepository.findByGroupId(10)).thenReturn(entities);
        when(converter.toDomainList(entities)).thenReturn(domainMembers);

        List<GroupMember> result = sut.getGroupMembersByGroupId(10);

        assertThat(result, hasSize(1));
        assertThat(result.get(0), is(domainMember));

        verify(jpaRepository).findByGroupId(10);
        verify(converter).toDomainList(entities);
    }
}
