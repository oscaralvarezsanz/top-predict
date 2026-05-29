package com.topleague.predict.infrastructure.in.controller;

import com.topleague.predict.application.port.in.group.GetMyGroupsUseCase;
import com.topleague.predict.domain.model.AppUser;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.infrastructure.config.security.AppUserDetails;
import com.topleague.predict.infrastructure.in.mapper.GroupWebConverter;
import com.topleague.predict.infrastructure.in.model.WebGroupResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

class MeControllerTest {

    private static final Integer USER_ID = 42;
    private static final String USERNAME = "john_doe";

    private final GetMyGroupsUseCase getMyGroupsUseCase = mock(GetMyGroupsUseCase.class);
    private final GroupWebConverter groupWebConverter = mock(GroupWebConverter.class);
    private final MeController controller = new MeController(getMyGroupsUseCase, groupWebConverter);

    @BeforeEach
    void setup() {
        AppUserDetails principal = new AppUserDetails(AppUser.builder().id(USER_ID).username(USERNAME).build());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getMyGroupsShouldReturnListWith200Ok() {
        Group group1 = Group.builder().id(1).name("Group 1").build();
        Group group2 = Group.builder().id(2).name("Group 2").build();
        List<Group> domainGroups = Arrays.asList(group1, group2);

        WebGroupResponse response1 = WebGroupResponse.builder().id(1).name("Group 1").build();
        WebGroupResponse response2 = WebGroupResponse.builder().id(2).name("Group 2").build();

        when(getMyGroupsUseCase.getMyGroups(USER_ID)).thenReturn(domainGroups);
        when(groupWebConverter.toWebResponse(group1)).thenReturn(response1);
        when(groupWebConverter.toWebResponse(group2)).thenReturn(response2);

        ResponseEntity<List<WebGroupResponse>> response = controller.getMyGroups();

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(Arrays.asList(response1, response2)));

        verify(getMyGroupsUseCase).getMyGroups(USER_ID);
        verify(groupWebConverter).toWebResponse(group1);
        verify(groupWebConverter).toWebResponse(group2);
    }
}
