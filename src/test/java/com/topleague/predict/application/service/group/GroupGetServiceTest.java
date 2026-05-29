package com.topleague.predict.application.service.group;

import com.topleague.predict.application.port.out.group.GroupGetByUserIdRepository;
import com.topleague.predict.domain.model.Group;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class GroupGetServiceTest {

    private final GroupGetByUserIdRepository repository = mock(GroupGetByUserIdRepository.class);
    private final GroupGetService service = new GroupGetService(repository);

    @Test
    void shouldReturnGroupsForUser() {
        Integer userId = 42;
        Group group1 = Group.builder().id(1).name("Group 1").build();
        Group group2 = Group.builder().id(2).name("Group 2").build();
        List<Group> expectedGroups = Arrays.asList(group1, group2);

        when(repository.getGroupsByUserId(userId)).thenReturn(expectedGroups);

        List<Group> actualGroups = service.getMyGroups(userId);

        assertThat(actualGroups, is(expectedGroups));
        verify(repository).getGroupsByUserId(userId);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoGroups() {
        Integer userId = 42;
        when(repository.getGroupsByUserId(userId)).thenReturn(Collections.emptyList());

        List<Group> actualGroups = service.getMyGroups(userId);

        assertThat(actualGroups.isEmpty(), is(true));
        verify(repository).getGroupsByUserId(userId);
    }
}
