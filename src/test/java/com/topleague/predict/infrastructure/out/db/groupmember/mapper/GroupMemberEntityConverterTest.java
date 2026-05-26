package com.topleague.predict.infrastructure.out.db.groupmember.mapper;

import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.infrastructure.out.db.model.GroupMemberEntity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class GroupMemberEntityConverterTest {

    private final GroupMemberEntityConverter sut = new GroupMemberEntityConverterImpl();

    @Test
    void shouldMapGroupMemberEntityToDomainModel() {
        GroupMemberEntity entity = GroupMemberEntity.builder()
                .id(1)
                .groupId(10)
                .userId(100)
                .alias("Alias")
                .totalPoints(15)
                .build();

        GroupMember result = sut.toDomain(entity);

        assertThat(result, notNullValue());
        assertThat(result.getId(), is(1));
        assertThat(result.getGroupId(), is(10));
        assertThat(result.getUserId(), is(100));
        assertThat(result.getAlias(), is("Alias"));
        assertThat(result.getTotalPoints(), is(15));
        assertThat(result.getRank(), nullValue());
    }

    @Test
    void shouldMapGroupMemberDomainModelToEntity() {
        GroupMember domain = GroupMember.builder()
                .id(1)
                .groupId(10)
                .userId(100)
                .alias("Alias")
                .totalPoints(15)
                .rank(2) // transient, shouldn't matter to entity but verified
                .build();

        GroupMemberEntity result = sut.toEntity(domain);

        assertThat(result, notNullValue());
        assertThat(result.getId(), is(1));
        assertThat(result.getGroupId(), is(10));
        assertThat(result.getUserId(), is(100));
        assertThat(result.getAlias(), is("Alias"));
        assertThat(result.getTotalPoints(), is(15));
    }

    @Test
    void shouldMapEntitiesListToDomainList() {
        GroupMemberEntity entity = GroupMemberEntity.builder()
                .id(1)
                .groupId(10)
                .userId(100)
                .alias("Alias")
                .totalPoints(15)
                .build();

        List<GroupMember> result = sut.toDomainList(Arrays.asList(entity));

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), is(1));
        assertThat(result.get(0).getGroupId(), is(10));
        assertThat(result.get(0).getUserId(), is(100));
        assertThat(result.get(0).getAlias(), is("Alias"));
        assertThat(result.get(0).getTotalPoints(), is(15));
    }
}
