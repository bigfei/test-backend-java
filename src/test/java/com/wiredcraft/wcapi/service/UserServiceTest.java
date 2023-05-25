package com.wiredcraft.wcapi.service;

import com.wiredcraft.wcapi.exception.UserRegistrationException;
import com.wiredcraft.wcapi.model.Address;
import com.wiredcraft.wcapi.model.User;
import com.wiredcraft.wcapi.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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
        final User user = new User("Tom", LocalDate.now(), new Address("ADDR1"), "T1");
        given(userRepository.findByName(user.getName())).willReturn(Optional.of(user));
        assertThrows(UserRegistrationException.class, () -> {
            userService.createUser(user);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateUser() {
        final User user = new User("Tom", LocalDate.now(), new Address("ADDR1"), "T1");
        user.setId("111a");
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);
        final User expected = userService.updateUser(user);
        assertThat(expected).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldReturnFindAll() {
        List<User> users = new ArrayList<>();
        users.add(new User("Tom", LocalDate.now(), new Address("ADDR1"), "T1"));
        users.add(new User("James", LocalDate.now(), new Address("ADDR2"), "T2"));
        users.add(new User("Lisa", LocalDate.now(), new Address("ADDR3"), "T3"));

        Pageable paging = PageRequest.of(0, 10);
        Page<User> expected = new PageImpl<>(users);

        given(userRepository.findAll(paging)).willReturn(expected);
        Page<User> actual = userService.getAllUsers(paging);

        assertEquals(3, expected.getTotalElements());
        assertEquals(users, expected.getContent());
    }

    @Test
    void findUserById() {
        String id = "1a";
        final User user = new User("Tom", LocalDate.now(), new Address("ADDR1"), "T1");

        given(userRepository.findById(id)).willReturn(Optional.of(user));

        Optional<User> expected = userService.getUserById(id);

        assertThat(expected).isNotNull();
        assertEquals("Tom", expected.get().getName());
    }

    @Test
    void shouldBeDelete() {
        final String userId = "1a";

        userService.deleteUser(userId);
        userService.deleteUser(userId);

        verify(userRepository, times(2)).deleteById(userId);
    }

}
