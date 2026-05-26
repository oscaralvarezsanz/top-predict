package com.topleague.predict.infrastructure.out.db.group;

import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.out.db.group.mapper.GroupEntityConverter;
import com.topleague.predict.infrastructure.out.db.model.GroupEntity;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class MySqlGroupGetByIdRepositoryTest {

    private final JpaGroupRepository jpaRepository = mock(JpaGroupRepository.class);
    private final GroupEntityConverter converter = mock(GroupEntityConverter.class);
    private final MySqlGroupGetByIdRepository sut = new MySqlGroupGetByIdRepository(jpaRepository, converter);

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
}
