package com.wiredcraft.wcapi.service;

import com.wiredcraft.wcapi.exception.UserRegistrationException;
import com.wiredcraft.wcapi.model.User;
import com.wiredcraft.wcapi.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSavedUserSuccess() {
        final User user = new User();
        given(userRepository.save(user)).willAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.createUser(user);
        assertThat(savedUser).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowErrorWhenSaveUserWithExistingName() {
        final User user = new User("Tom", LocalDate.now(),"ADDR1","T1");
        given(userRepository.findByName(user.getName())).willReturn(Optional.of(user));
        assertThrows(UserRegistrationException.class,() -> {
            userService.createUser(user);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser() {
        final User user = new User("Tom", LocalDate.now(),"ADDR1","T1");
        user.setId("111a");
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);
        final User expected = userService.updateUser(user);
        assertThat(expected).isNotNull();
        verify(userRepository).save(any(User.class));
    }
}
