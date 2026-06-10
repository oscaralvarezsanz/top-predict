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

class MySqlGroupMemberRepositoryTest {

    private final JpaGroupMemberRepository jpaRepository = mock(JpaGroupMemberRepository.class);
    private final GroupMemberEntityConverter converter = mock(GroupMemberEntityConverter.class);
    private final MySqlGroupMemberRepository sut = new MySqlGroupMemberRepository(jpaRepository, converter);

    @Test
    void saveGroupMemberShouldDelegateToJpaRepositoryAndReturnDomainGroupMember() {
        GroupMember domainMember = GroupMember.builder().alias("john_doe").build();
        GroupMemberEntity entity = GroupMemberEntity.builder().alias("john_doe").build();
        GroupMember savedDomainMember = GroupMember.builder().id(1).alias("john_doe").build();

        when(converter.toEntity(domainMember)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(converter.toDomain(entity)).thenReturn(savedDomainMember);

        GroupMember result = sut.saveGroupMember(domainMember);

        assertThat(result, is(savedDomainMember));
        verify(converter).toEntity(domainMember);
        verify(jpaRepository).save(entity);
        verify(converter).toDomain(entity);
    }

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
