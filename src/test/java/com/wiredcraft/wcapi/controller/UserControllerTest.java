package com.wiredcraft.wcapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiredcraft.wcapi.model.User;
import com.wiredcraft.wcapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;


@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class})
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<User> userList;

    @BeforeEach
    void setUp() {
        this.userList = new ArrayList<>();
        this.userList.add(new User("Tom", LocalDate.now(), "ADDR1", "T1"));
        this.userList.add(new User("Jack", LocalDate.now(), "ADDR2", "T2"));
        this.userList.add(new User("Peter", LocalDate.now(), "ADDR3", "T3"));
    }

    @Test
    void shouldFetchAllUsers() throws Exception {
        Pageable paging = PageRequest.of(0, 10);
        Page<User> expected = new PageImpl(userList);

        given(userService.getAllUsers(paging)).willReturn(expected);

        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(userList.size())));
    }
}
