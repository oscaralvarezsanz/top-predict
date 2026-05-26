package com.topleague.predict.infrastructure.in.mapper;

import com.topleague.predict.domain.model.GroupLeaderboard;
import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.infrastructure.in.model.WebGroupMemberStanding;
import com.topleague.predict.infrastructure.in.model.WebLeaderboardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class LeaderboardWebConverterTest {

    private final LeaderboardWebConverterImpl sut = new LeaderboardWebConverterImpl();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(sut, "groupMemberWebConverter", new GroupMemberWebConverterImpl());
    }

    @Test
    void shouldMapGroupLeaderboardToWebLeaderboardResponse() {
        GroupMember member = GroupMember.builder()
                .userId(5)
                .alias("Winner")
                .totalPoints(100)
                .rank(1)
                .build();

        GroupLeaderboard leaderboard = GroupLeaderboard.builder()
                .groupId(42)
                .groupName("Elite Group")
                .members(Arrays.asList(member))
                .build();

        WebLeaderboardResponse result = sut.toWebResponse(leaderboard);

        assertThat(result, notNullValue());
        assertThat(result.getGroupId(), is(42));
        assertThat(result.getGroupName(), is("Elite Group"));
        assertThat(result.getMembers(), hasSize(1));

        WebGroupMemberStanding webMember = result.getMembers().get(0);
        assertThat(webMember.getUserId(), is(5));
        assertThat(webMember.getAlias(), is("Winner"));
        assertThat(webMember.getTotalPoints(), is(100));
        assertThat(webMember.getRank(), is(1));
    }
}
