package com.topleague.predict.application.service.prediction;

import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.application.port.out.prediction.PredictionGetByGroupAndUserRepository;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.GroupMemberException;
import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.domain.model.Prediction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PredictionGetServiceTest {

    private final GroupMemberGetByGroupIdRepository groupMemberRepository = mock(GroupMemberGetByGroupIdRepository.class);
    private final PredictionGetByGroupAndUserRepository predictionRepository = mock(PredictionGetByGroupAndUserRepository.class);

    private final PredictionGetService service = new PredictionGetService(
            groupMemberRepository,
            predictionRepository
    );

    @Test
    void getMyGroupPredictionsShouldReturnPredictionsWhenGroupExistsAndUserIsMember() {
        Integer groupId = 1;
        Integer userId = 42;

        GroupMember member = GroupMember.builder().groupId(groupId).userId(userId).build();
        Prediction prediction1 = Prediction.builder().id(10).groupId(groupId).userId(userId).predictedHomeScore(2).predictedAwayScore(1).build();
        Prediction prediction2 = Prediction.builder().id(11).groupId(groupId).userId(userId).predictedHomeScore(1).predictedAwayScore(1).build();
        List<Prediction> expectedPredictions = Arrays.asList(prediction1, prediction2);

        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Collections.singletonList(member));
        when(predictionRepository.getPredictionsByGroupAndUser(groupId, userId)).thenReturn(expectedPredictions);

        List<Prediction> result = service.getMyGroupPredictions(groupId, userId);

        assertThat(result, is(expectedPredictions));
        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verify(predictionRepository).getPredictionsByGroupAndUser(groupId, userId);
    }

    @Test
    void getMyGroupPredictionsShouldThrowForbiddenWhenUserIsNotGroupMember() {
        Integer groupId = 1;
        Integer userId = 42;

        GroupMember otherMember = GroupMember.builder().groupId(groupId).userId(99).build();

        when(groupMemberRepository.getGroupMembersByGroupId(groupId)).thenReturn(Collections.singletonList(otherMember));

        GroupMemberException exception = assertThrows(GroupMemberException.class, () ->
                service.getMyGroupPredictions(groupId, userId)
        );

        assertThat(exception.getErrorCode(), is(GroupMemberErrorCode.MEMBER_NOT_FOUND));
        verify(groupMemberRepository).getGroupMembersByGroupId(groupId);
        verifyNoInteractions(predictionRepository);
    }
}
