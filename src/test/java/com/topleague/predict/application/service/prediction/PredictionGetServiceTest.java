package com.topleague.predict.application.service.prediction;

import com.topleague.predict.application.port.out.group.GroupGetByIdRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.application.port.out.prediction.PredictionGetByGroupAndUserRepository;
import com.topleague.predict.domain.exception.GroupErrorCode;
import com.topleague.predict.domain.exception.GroupException;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.GroupMemberException;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.domain.model.Prediction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PredictionGetServiceTest {

    private final GroupGetByIdRepository groupRepository = mock(GroupGetByIdRepository.class);
    private final GroupMemberGetByGroupIdRepository groupMemberRepository = mock(GroupMemberGetByGroupIdRepository.class);
    private final PredictionGetByGroupAndUserRepository predictionRepository = mock(PredictionGetByGroupAndUserRepository.class);

    private final PredictionGetService service = new PredictionGetService(
            groupRepository,
            groupMemberRepository,
            predictionRepository
    );

    @Test
    void getMyGroupPredictionsShouldReturnPredictionsWhenGroupExistsAndUserIsMember() {
        Integer groupId = 1;
        Integer userId = 42;

        Group group = Group.builder().id(groupId).name("Group A").build();
        GroupMember member = GroupMember.builder().groupId(groupId).userId(userId).build();
        Prediction prediction1 = Prediction.builder().id(10).groupId(groupId).userId(userId).predictedHomeScore(2).predictedAwayScore(1).build();
        Prediction prediction2 = Prediction.builder().id(11).groupId(groupId).userId(userId).predictedHomeScore(1).predictedAwayScore(1).build();
        List<Prediction> expectedPredictions = Arrays.asList(prediction1, prediction2);

        when(groupRepository.getGroupById(groupId)).thenReturn(Optional.of(group));
        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Collections.singletonList(member));
        when(predictionRepository.getPredictionsByGroupAndUser(groupId, userId)).thenReturn(expectedPredictions);

        List<Prediction> result = service.getMyGroupPredictions(groupId, userId);

        assertThat(result, is(expectedPredictions));
        verify(groupRepository).getGroupById(groupId);
        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verify(predictionRepository).getPredictionsByGroupAndUser(groupId, userId);
    }

    @Test
    void getMyGroupPredictionsShouldThrowGroupNotFoundWhenGroupDoesNotExist() {
        Integer groupId = 1;
        Integer userId = 42;

        when(groupRepository.getGroupById(groupId)).thenReturn(Optional.empty());

        GroupException exception = assertThrows(GroupException.class, () ->
                service.getMyGroupPredictions(groupId, userId)
        );

        assertThat(exception.getErrorCode(), is(GroupErrorCode.GROUP_NOT_FOUND));
        verify(groupRepository).getGroupById(groupId);
        verifyNoInteractions(groupMemberRepository, predictionRepository);
    }

    @Test
    void getMyGroupPredictionsShouldThrowForbiddenWhenUserIsNotGroupMember() {
        Integer groupId = 1;
        Integer userId = 42;

        Group group = Group.builder().id(groupId).name("Group A").build();
        GroupMember otherMember = GroupMember.builder().groupId(groupId).userId(99).build();

        when(groupRepository.getGroupById(groupId)).thenReturn(Optional.of(group));
        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Collections.singletonList(otherMember));

        GroupMemberException exception = assertThrows(GroupMemberException.class, () ->
                service.getMyGroupPredictions(groupId, userId)
        );

        assertThat(exception.getErrorCode(), is(GroupMemberErrorCode.MEMBER_NOT_FOUND));
        verify(groupRepository).getGroupById(groupId);
        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verifyNoInteractions(predictionRepository);
    }
}
