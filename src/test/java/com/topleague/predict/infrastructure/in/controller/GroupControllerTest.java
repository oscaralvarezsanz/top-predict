package com.topleague.predict.infrastructure.in.controller;

import com.topleague.predict.application.port.in.group.CreateGroupUseCase;
import com.topleague.predict.domain.model.AppUser;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.mapper.GroupWebConverter;
import com.topleague.predict.infrastructure.in.model.WebGroupCreateRequest;
import com.topleague.predict.infrastructure.in.model.WebGroupResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class GroupControllerTest {

    private static final Integer OWNER_ID = 42;
    private static final String USERNAME = "john_doe";

    private final CreateGroupUseCase createUseCase = mock(CreateGroupUseCase.class);
    private final GroupWebConverter converter = mock(GroupWebConverter.class);
    private final GroupController controller = new GroupController(createUseCase, converter);

    @BeforeEach
    void setup() {
        AppUserDetails principal = new AppUserDetails(AppUser.builder().id(OWNER_ID).username(USERNAME).build());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createGroupShouldInjectOwnerIdFromTokenAndReturn201Created() {

        WebGroupCreateRequest webRequest = WebGroupCreateRequest.builder().name("Group A").leagueId(5).build();
        Group groupFromConverter = Group.builder().name("Group A").leagueId(5).build();
        Group groupWithOwner = Group.builder().name("Group A").leagueId(5).ownerId(OWNER_ID).build();
        Group createdGroup = Group.builder().id(10).name("Group A").leagueId(5).ownerId(OWNER_ID).build();
        WebGroupResponse webResponse = WebGroupResponse.builder().id(10).name("Group A").ownerId(OWNER_ID).build();

        when(converter.toDomain(webRequest)).thenReturn(groupFromConverter);
        when(createUseCase.createGroup(groupWithOwner, USERNAME)).thenReturn(createdGroup);
        when(converter.toWebResponse(createdGroup)).thenReturn(webResponse);

        ResponseEntity<WebGroupResponse> response = controller.createGroup(webRequest);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody(), is(webResponse));

        verify(converter).toDomain(webRequest);
        verify(createUseCase).createGroup(groupWithOwner, USERNAME);
        verify(converter).toWebResponse(createdGroup);
    }
}
