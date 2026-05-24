package com.topleague.predict.infrastructure.out.db.groupmember;

import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.infrastructure.out.db.groupmember.mapper.GroupMemberEntityConverter;
import com.topleague.predict.infrastructure.out.db.model.GroupMemberEntity;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class MySqlGroupMemberCreateRepositoryTest {

    private final JpaGroupMemberRepository jpaRepository = mock(JpaGroupMemberRepository.class);
    private final GroupMemberEntityConverter converter = mock(GroupMemberEntityConverter.class);
    private final MySqlGroupMemberCreateRepository sut = new MySqlGroupMemberCreateRepository(jpaRepository, converter);

    @Test
    void createGroupMemberShouldDelegateToJpaRepositoryAndReturnDomainGroupMember() {
        GroupMember domainMember = GroupMember.builder().alias("john_doe").build();
        GroupMemberEntity entity = GroupMemberEntity.builder().alias("john_doe").build();
        GroupMember savedDomainMember = GroupMember.builder().id(1).alias("john_doe").build();

        when(converter.toEntity(domainMember)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(converter.toDomain(entity)).thenReturn(savedDomainMember);

        GroupMember result = sut.createGroupMember(domainMember);

        assertThat(result, is(savedDomainMember));
        verify(converter).toEntity(domainMember);
        verify(jpaRepository).save(entity);
        verify(converter).toDomain(entity);
    }
}
