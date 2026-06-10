package com.topleague.predict.application.service.group;

import com.topleague.predict.application.port.out.group.GroupGetByInviteCodeRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberSaveRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.domain.exception.GroupErrorCode;
import com.topleague.predict.domain.exception.GroupException;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.GroupMemberException;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupMember;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GroupJoinServiceTest {

    private final GroupGetByInviteCodeRepository groupGetByInviteCodeRepository = mock(GroupGetByInviteCodeRepository.class);
    private final GroupMemberGetByGroupIdRepository groupMemberGetByGroupIdRepository = mock(GroupMemberGetByGroupIdRepository.class);
    private final GroupMemberSaveRepository groupMemberSaveRepository = mock(GroupMemberSaveRepository.class);
    private final GroupJoinService service = new GroupJoinService(
            groupGetByInviteCodeRepository,
            groupMemberGetByGroupIdRepository,
            groupMemberSaveRepository
    );

    @Test
    void shouldSuccessfullyJoinGroup() {
        String code = "INVITE88";
        Group group = Group.builder().id(10).name("League A").inviteCode(code).build();

        when(groupGetByInviteCodeRepository.getGroupByInviteCode(code)).thenReturn(Optional.of(group));
        when(groupMemberGetByGroupIdRepository.getGroupMembersByGroupId(10)).thenReturn(Collections.emptyList());
        when(groupMemberSaveRepository.saveGroupMember(any(GroupMember.class))).thenAnswer(inv -> inv.getArgument(0));

        Group result = service.joinGroup(code, 42, "user_alias");

        assertThat(result, is(group));
        verify(groupGetByInviteCodeRepository).getGroupByInviteCode(code);
        verify(groupMemberGetByGroupIdRepository).getGroupMembersByGroupId(10);
        verify(groupMemberSaveRepository).saveGroupMember(argThat(member ->
                member.getGroupId().equals(10) &&
                member.getUserId().equals(42) &&
                member.getAlias().equals("user_alias") &&
                member.getTotalPoints() == 0
        ));
    }

    @Test
    void shouldThrowGroupExceptionWhenInviteCodeNotFound() {
        String code = "NOTFOUND";
        when(groupGetByInviteCodeRepository.getGroupByInviteCode(code)).thenReturn(Optional.empty());

        GroupException exception = assertThrows(GroupException.class, () ->
                service.joinGroup(code, 42, "user_alias")
        );

        assertThat(exception.getErrorCode(), is(GroupErrorCode.GROUP_NOT_FOUND));
        verify(groupGetByInviteCodeRepository).getGroupByInviteCode(code);
        verifyNoInteractions(groupMemberGetByGroupIdRepository);
        verifyNoInteractions(groupMemberSaveRepository);
    }

    @Test
    void shouldThrowGroupMemberExceptionWhenUserIsAlreadyMember() {
        String code = "INVITE88";
        Group group = Group.builder().id(10).name("League A").inviteCode(code).build();
        GroupMember existingMember = GroupMember.builder().userId(42).alias("user_alias").build();

        when(groupGetByInviteCodeRepository.getGroupByInviteCode(code)).thenReturn(Optional.of(group));
        when(groupMemberGetByGroupIdRepository.getGroupMembersByGroupId(10)).thenReturn(Arrays.asList(existingMember));

        GroupMemberException exception = assertThrows(GroupMemberException.class, () ->
                service.joinGroup(code, 42, "user_alias")
        );

        assertThat(exception.getErrorCode(), is(GroupMemberErrorCode.MEMBER_EXISTS));
        verify(groupGetByInviteCodeRepository).getGroupByInviteCode(code);
        verify(groupMemberGetByGroupIdRepository).getGroupMembersByGroupId(10);
        verifyNoInteractions(groupMemberSaveRepository);
    }
}
