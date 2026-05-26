package com.topleague.predict.application.service.group;

import com.topleague.predict.application.port.out.group.GroupGetByIdRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.domain.exception.GroupErrorCode;
import com.topleague.predict.domain.exception.GroupException;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.GroupMemberException;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupLeaderboard;
import com.topleague.predict.domain.model.GroupMember;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GroupLeaderboardServiceTest {

    private final GroupGetByIdRepository groupRepository = mock(GroupGetByIdRepository.class);
    private final GroupMemberGetByGroupIdRepository groupMemberRepository = mock(GroupMemberGetByGroupIdRepository.class);
    private final GroupLeaderboardService service = new GroupLeaderboardService(groupRepository, groupMemberRepository);

    @Test
    void shouldThrowGroupExceptionWhenGroupDoesNotExist() {
        when(groupRepository.getGroupById(1)).thenReturn(Optional.empty());

        GroupException exception = assertThrows(GroupException.class, () ->
                service.getGroupLeaderboard(1, 100)
        );

        assertThat(exception.getErrorCode(), is(GroupErrorCode.GROUP_NOT_FOUND));
        verify(groupRepository).getGroupById(1);
        verifyNoInteractions(groupMemberRepository);
    }

    @Test
    void shouldThrowGroupMemberExceptionWhenUserIsNotAMember() {
        Group group = Group.builder().id(1).name("Test Group").build();
        GroupMember member1 = GroupMember.builder().userId(10).alias("User 1").totalPoints(10).build();
        GroupMember member2 = GroupMember.builder().userId(20).alias("User 2").totalPoints(15).build();

        when(groupRepository.getGroupById(1)).thenReturn(Optional.of(group));
        when(groupMemberRepository.getGroupMembersByGroupId(1)).thenReturn(Arrays.asList(member1, member2));

        GroupMemberException exception = assertThrows(GroupMemberException.class, () ->
                service.getGroupLeaderboard(1, 100) // 100 is not in the group members
        );

        assertThat(exception.getErrorCode(), is(GroupMemberErrorCode.MEMBER_NOT_FOUND));
        verify(groupRepository).getGroupById(1);
        verify(groupMemberRepository).getGroupMembersByGroupId(1);
    }

    @Test
    void shouldCalculateStandardCompetitionRankingForMembers() {
        Group group = Group.builder().id(1).name("Championship Group").build();

        GroupMember member1 = GroupMember.builder().userId(1).alias("Alice").totalPoints(25).build();
        GroupMember member2 = GroupMember.builder().userId(2).alias("Bob").totalPoints(25).build();
        GroupMember member3 = GroupMember.builder().userId(3).alias("Charlie").totalPoints(18).build();
        GroupMember member4 = GroupMember.builder().userId(4).alias("David").totalPoints(10).build();

        when(groupRepository.getGroupById(1)).thenReturn(Optional.of(group));
        when(groupMemberRepository.getGroupMembersByGroupId(1)).thenReturn(Arrays.asList(member3, member1, member4, member2));

        GroupLeaderboard result = service.getGroupLeaderboard(1, 2);

        assertThat(result.getGroupId(), is(1));
        assertThat(result.getGroupName(), is("Championship Group"));

        List<GroupMember> rankedMembers = result.getMembers();
        assertThat(rankedMembers, hasSize(4));

        assertThat(rankedMembers.get(0).getTotalPoints(), is(25));
        assertThat(rankedMembers.get(0).getRank(), is(1));

        assertThat(rankedMembers.get(1).getTotalPoints(), is(25));
        assertThat(rankedMembers.get(1).getRank(), is(1));

        assertThat(rankedMembers.get(2).getTotalPoints(), is(18));
        assertThat(rankedMembers.get(2).getRank(), is(3));

        assertThat(rankedMembers.get(3).getTotalPoints(), is(10));
        assertThat(rankedMembers.get(3).getRank(), is(4));
    }
}
