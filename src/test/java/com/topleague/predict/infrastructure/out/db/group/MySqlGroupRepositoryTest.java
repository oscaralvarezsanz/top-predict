package com.topleague.predict.infrastructure.out.db.group;

import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.out.db.group.mapper.GroupEntityConverter;
import com.topleague.predict.infrastructure.out.db.model.GroupEntity;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class MySqlGroupRepositoryTest {

    private final JpaGroupRepository jpaRepository = mock(JpaGroupRepository.class);
    private final GroupEntityConverter converter = mock(GroupEntityConverter.class);
    private final MySqlGroupRepository sut = new MySqlGroupRepository(jpaRepository, converter);

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

    @Test
    void getGroupByIdShouldDelegateToJpaRepositoryAndConvertResult() {
        GroupEntity entity = GroupEntity.builder().id(1).name("My Group").build();
        Group domainGroup = Group.builder().id(1).name("My Group").build();

        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));
        when(converter.toDomain(entity)).thenReturn(domainGroup);

        Optional<Group> result = sut.getGroupById(1);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(domainGroup));

        verify(jpaRepository).findById(1);
        verify(converter).toDomain(entity);
    }

    @Test
    void getGroupByInviteCodeShouldDelegateToJpaRepositoryAndConvertResult() {
        String code = "XYZ12345";
        GroupEntity entity = GroupEntity.builder().id(2).name("Shared Group").inviteCode(code).build();
        Group domainGroup = Group.builder().id(2).name("Shared Group").inviteCode(code).build();

        when(jpaRepository.findByInviteCode(code)).thenReturn(Optional.of(entity));
        when(converter.toDomain(entity)).thenReturn(domainGroup);

        Optional<Group> result = sut.getGroupByInviteCode(code);

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(domainGroup));

        verify(jpaRepository).findByInviteCode(code);
        verify(converter).toDomain(entity);
    }
}
