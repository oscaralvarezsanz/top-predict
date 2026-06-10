package com.topleague.predict.application.service.group;

import com.topleague.predict.application.port.out.group.GroupCreateRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberSaveRepository;
import com.topleague.predict.domain.exception.GroupErrorCode;
import com.topleague.predict.domain.exception.GroupException;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupMember;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GroupCreateServiceTest {

    private final GroupCreateRepository groupCreateRepository = mock(GroupCreateRepository.class);
    private final GroupMemberSaveRepository groupMemberSaveRepository = mock(GroupMemberSaveRepository.class);
    private final GroupCreateService service = new GroupCreateService(groupCreateRepository, groupMemberSaveRepository);

    @Test
    void shouldCreateGroupAndAutoRegisterOwner() {
        Group groupToCreate = Group.builder()
                .name("Champions Group")
                .ownerId(42)
                .leagueId(10)
                .build();

        Group savedGroup = groupToCreate.toBuilder()
                .id(1)
                .inviteCode("ABCDEF12")
                .build();

        when(groupCreateRepository.existsByInviteCode(anyString())).thenReturn(false);
        when(groupCreateRepository.createGroup(any(Group.class))).thenReturn(savedGroup);
        when(groupMemberSaveRepository.saveGroupMember(any(GroupMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Group result = service.createGroup(groupToCreate, "john_doe");

        assertThat(result, is(savedGroup));

        verify(groupCreateRepository).createGroup(argThat(g -> 
                g.getName().equals("Champions Group") &&
                g.getOwnerId().equals(42) &&
                g.getLeagueId().equals(10) &&
                g.getInviteCode() != null &&
                g.getCreatedAt() != null
        ));

        verify(groupMemberSaveRepository).saveGroupMember(argThat(member -> 
                member.getGroupId().equals(1) &&
                member.getUserId().equals(42) &&
                member.getAlias().equals("john_doe") &&
                member.getTotalPoints() == 0
        ));
    }

    @Test
    void shouldThrowGroupExceptionWhenInviteCodeGenerationFails() {
        Group groupToCreate = Group.builder()
                .name("Collision Group")
                .ownerId(42)
                .leagueId(10)
                .build();

        when(groupCreateRepository.existsByInviteCode(anyString())).thenReturn(true);

        GroupException exception = assertThrows(GroupException.class, () -> 
                service.createGroup(groupToCreate, "john_doe")
        );

        assertThat(exception.getErrorCode(), is(GroupErrorCode.COULD_NOT_GENERATE_INVITE_CODE));
        verify(groupCreateRepository, times(10)).existsByInviteCode(anyString());
        verify(groupCreateRepository, never()).createGroup(any(Group.class));
    }
}
