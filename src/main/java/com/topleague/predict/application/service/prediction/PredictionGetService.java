package com.topleague.predict.application.service.prediction;

import com.topleague.predict.application.port.in.prediction.GetMyGroupPredictionsUseCase;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.application.port.out.prediction.PredictionGetByGroupAndUserRepository;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.GroupMemberException;
import com.topleague.predict.domain.model.Prediction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PredictionGetService implements GetMyGroupPredictionsUseCase {

    private final GroupMemberGetByGroupIdRepository groupMemberGetByGroupIdRepository;
    private final PredictionGetByGroupAndUserRepository predictionGetByGroupAndUserRepository;

    public PredictionGetService(
            GroupMemberGetByGroupIdRepository groupMemberGetByGroupIdRepository,
            PredictionGetByGroupAndUserRepository predictionGetByGroupAndUserRepository) {
        this.groupMemberGetByGroupIdRepository = groupMemberGetByGroupIdRepository;
        this.predictionGetByGroupAndUserRepository = predictionGetByGroupAndUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prediction> getMyGroupPredictions(Integer groupId, Integer userId) {
        if (!isUserAGroupMember(groupId, userId)) {
            throw new GroupMemberException(GroupMemberErrorCode.MEMBER_NOT_FOUND);
        }

        return predictionGetByGroupAndUserRepository.getPredictionsByGroupAndUser(groupId, userId);
    }

    private boolean isUserAGroupMember(Integer groupId, Integer userId) {
        return groupMemberGetByGroupIdRepository.getGroupMembersByGroupId(groupId).stream()
                .anyMatch(member -> member.getUserId().equals(userId));
    }
}
