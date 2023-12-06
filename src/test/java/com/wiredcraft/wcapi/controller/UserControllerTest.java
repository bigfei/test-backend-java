package com.wiredcraft.wcapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiredcraft.wcapi.exception.UserRegistrationException;
import com.wiredcraft.wcapi.model.Address;
import com.wiredcraft.wcapi.model.User;
import com.wiredcraft.wcapi.service.FollowService;
import com.wiredcraft.wcapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class,
        SecurityAutoConfiguration.class})
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FollowService followService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<User> userList;

    @BeforeEach
    void setUp() {
        this.userList = new ArrayList<>();
        this.userList.add(new User("Tom", LocalDate.now(), new Address("ADDR1"), "T1"));
        this.userList.add(new User("Jack", LocalDate.now(), new Address("ADDR2"), "T2"));
        this.userList.add(new User("Peter", LocalDate.now(), new Address("ADDR3"), "T3"));
    }

    /**
     * This test case is to test the scenario when all users are found.
     * @throws Exception exception thrown
     */
    @Test
    void shouldFetchAllUsers() throws Exception {
        Pageable paging = PageRequest.of(0, 3);
        Page<User> expected = new PageImpl<>(userList);

        given(userService.getAllUsers(paging)).willReturn(expected);

        this.mockMvc.perform(get("/users")).andExpect(status().isOk()).andExpect(jsonPath("$.data.size()", is(userList.size())));
    }

    /**
     * This test case is to test the scenario when the user is found by the given id.
     * @throws Exception exception thrown
     */
    @Test
    void shouldFetchOneUserById() throws Exception {

        final String userId = "11a";
        final User user = new User("Tom", LocalDate.now(), new Address("ADDR1"), "T1");

        given(userService.getUserById(userId)).willReturn(Optional.of(user));

        this.mockMvc.perform(get("/users/{id}", userId)).andExpect(status().isOk()).andExpect(jsonPath("$.name", is(user.getName())));
    }

    /**
     * This test case is to test the scenario when the user is not found by the given id.
     * @throws Exception exception
     */
    @Test
    void shouldReturn404WhenFindUserById() throws Exception {
        final String userId = "11a";
        given(userService.getUserById(userId)).willReturn(Optional.empty());
        this.mockMvc.perform(get("/users/{id}", userId)).andExpect(status().isNotFound());
    }


    @Test
    void shouldCreateNewUser() throws Exception {
        given(userService.createUser(any(User.class))).willAnswer((invocation) -> invocation.getArgument(0));

        final User user = new User("Tom", LocalDate.now(), new Address("ADDR1"), "T1");

        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(user.getName())))
        ;
    }

    @Test
    void shouldReturn400WhenCreateNewUserWithoutEmail() throws Exception {
        given(userService.createUser(any(User.class))).willThrow(new UserRegistrationException("Jane Smith"));
        final User user = new User("Jane Smith", LocalDate.now(), new Address("ADDR1"), "T1");

        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(res -> assertTrue(res.getResolvedException() instanceof UserRegistrationException))
                .andExpect(content().string(containsString("Jane Smith")));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        String userId = "11a";
        final User user = new User("Tom", LocalDate.now(), new Address("ADDR1"), "T1");
        user.setId(userId);
        given(userService.getUserById(userId)).willReturn(Optional.of(user));
        given(userService.updateUser(any(User.class))).willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getName())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingUser() throws Exception {
        String userId = "11a";
        given(userService.getUserById(userId)).willReturn(Optional.empty());
        final User user = new User("Tom", LocalDate.now(), new Address("ADDR1"), "T1");

        this.mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        String userId = "11a";
        final User user = new User("Tom", LocalDate.now(), new Address("ADDR1"), "T1");
        user.setId(userId);
        given(userService.getUserById(userId)).willReturn(Optional.of(user));
        doNothing().when(userService).deleteUser(user.getId());

        this.mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getName())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingUser() throws Exception {
        String userId = "11a";
        given(userService.getUserById(userId)).willReturn(Optional.empty());

        this.mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound());

    }
}
