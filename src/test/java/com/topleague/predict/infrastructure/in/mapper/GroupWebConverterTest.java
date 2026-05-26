package com.topleague.predict.infrastructure.in.mapper;

import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.in.model.WebGroupCreateRequest;
import com.topleague.predict.infrastructure.in.model.WebGroupResponse;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class GroupWebConverterTest {

    private final GroupWebConverter sut = new GroupWebConverterImpl();

    @Test
    void shouldMapWebGroupCreateRequestToGroup() {
        WebGroupCreateRequest request = WebGroupCreateRequest.builder()
                .name("Test Group")
                .leagueId(10)
                .build();

        Group result = sut.toDomain(request);

        assertThat(result, notNullValue());
        assertThat(result.getName(), is("Test Group"));
        assertThat(result.getLeagueId(), is(10));
    }

    @Test
    void shouldMapGroupToWebGroupResponse() {
        Group group = Group.builder()
                .id(42)
                .name("Response Group")
                .ownerId(100)
                .leagueId(5)
                .build();

        WebGroupResponse result = sut.toWebResponse(group);

        assertThat(result, notNullValue());
        assertThat(result.getId(), is(42));
        assertThat(result.getName(), is("Response Group"));
        assertThat(result.getOwnerId(), is(100));
    }
}
