package com.topleague.predict.infrastructure.out.db.group;

import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.out.db.group.mapper.GroupEntityConverter;
import com.topleague.predict.infrastructure.out.db.model.GroupEntity;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class MySqlGroupCreateRepositoryTest {

    private final JpaGroupRepository jpaRepository = mock(JpaGroupRepository.class);
    private final GroupEntityConverter converter = mock(GroupEntityConverter.class);
    private final MySqlGroupCreateRepository sut = new MySqlGroupCreateRepository(jpaRepository, converter);

    @Test
    void createGroupShouldDelegateToJpaRepositoryAndReturnDomainGroup() {
        Group domainGroup = Group.builder().name("Group A").build();
        GroupEntity entity = GroupEntity.builder().name("Group A").build();
        Group savedDomainGroup = Group.builder().id(1).name("Group A").build();

        when(converter.toEntity(domainGroup)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(converter.toDomain(entity)).thenReturn(savedDomainGroup);

        Group result = sut.createGroup(domainGroup);

        assertThat(result, is(savedDomainGroup));
        verify(converter).toEntity(domainGroup);
        verify(jpaRepository).save(entity);
        verify(converter).toDomain(entity);
    }

    @Test
    void existsByInviteCodeShouldDelegateToJpaRepositoryAndReturnBoolean() {
        String code = "ABCDEF12";
        when(jpaRepository.existsByInviteCode(code)).thenReturn(true);

        boolean result = sut.existsByInviteCode(code);

        assertThat(result, is(true));
        verify(jpaRepository).existsByInviteCode(code);
    }
}
