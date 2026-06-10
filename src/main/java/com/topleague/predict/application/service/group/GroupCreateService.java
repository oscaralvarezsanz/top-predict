package com.topleague.predict.application.service.group;

import com.topleague.predict.application.port.in.group.CreateGroupUseCase;
import com.topleague.predict.application.port.out.group.GroupCreateRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberSaveRepository;
import com.topleague.predict.domain.exception.GroupErrorCode;
import com.topleague.predict.domain.exception.GroupException;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class GroupCreateService implements CreateGroupUseCase {

    private static final String INVITE_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final GroupCreateRepository groupCreateRepository;
    private final GroupMemberSaveRepository groupMemberSaveRepository;
    private final SecureRandom random = new SecureRandom();

    public GroupCreateService(GroupCreateRepository groupCreateRepository,
                              GroupMemberSaveRepository groupMemberSaveRepository) {
        this.groupCreateRepository = groupCreateRepository;
        this.groupMemberSaveRepository = groupMemberSaveRepository;
    }

    @Override
    @Transactional
    public Group createGroup(Group groupToCreate, String alias) {
        Group preparedGroup = groupToCreate.toBuilder()
                .inviteCode(generateUniqueInviteCode())
                .createdAt(LocalDateTime.now())
                .build();

        Group savedGroup = groupCreateRepository.createGroup(preparedGroup);

        GroupMember ownerMember = GroupMember.builder()
                .groupId(savedGroup.getId())
                .userId(savedGroup.getOwnerId())
                .alias(alias)
                .totalPoints(0)
                .build();
                
        groupMemberSaveRepository.saveGroupMember(ownerMember);

        return savedGroup;
    }

    private String generateUniqueInviteCode() {
        String code;
        int attempts = 0;
        do {
            code = generateInviteCode();
            attempts++;
            if (attempts > 10) {
                throw new GroupException(GroupErrorCode.COULD_NOT_GENERATE_INVITE_CODE);
            }
        } while (groupCreateRepository.existsByInviteCode(code));
        return code;
    }

    private String generateInviteCode() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(INVITE_CODE_CHARS.charAt(random.nextInt(INVITE_CODE_CHARS.length())));
        }
        return sb.toString();
    }
}
