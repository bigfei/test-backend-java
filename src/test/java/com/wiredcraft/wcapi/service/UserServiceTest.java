package com.wiredcraft.wcapi.service;

import com.wiredcraft.wcapi.exception.UserRegistrationException;
import com.wiredcraft.wcapi.model.Address;
import com.wiredcraft.wcapi.model.Follow;
import com.wiredcraft.wcapi.model.User;
import com.wiredcraft.wcapi.repos.FollowRepository;
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
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.*;

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

    @Mock
    private FollowRepository followRepository;

    @Mock
    private OAuth2User oauth2User;

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

        assertEquals(3, actual.getTotalElements());
        assertEquals(users, actual.getContent());
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

    @Test
    void shouldSyncAuth0UserWhenUserExists() {
        // Arrange
        String userName = "existingUser";
        String userSub = "auth0|123456";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", userName);
        attributes.put("sub", userSub);

        User existingUser = new User(userName, LocalDate.now(), new Address("ADDR1"), "original_desc");
        existingUser.setId("user123");

        given(oauth2User.getAttributes()).willReturn(attributes);
        given(userRepository.findByName(userName)).willReturn(Optional.of(existingUser));
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // Act
        userService.syncAuth0User(oauth2User);

        // Assert
        verify(userRepository).findByName(userName);
        verify(userRepository).save(argThat(user -> user.getDescription().equals(userSub)));
    }

    @Test
    void shouldSyncAuth0UserWhenUserDoesNotExist() {
        // Arrange
        String userName = "newUser";
        String userSub = "auth0|789012";
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", userName);
        attributes.put("sub", userSub);

        given(oauth2User.getAttributes()).willReturn(attributes);
        given(userRepository.findByName(userName)).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // Act
        userService.syncAuth0User(oauth2User);

        // Assert
        verify(userRepository).findByName(userName);
        verify(userRepository).save(argThat(user ->
            user.getName().equals(userName) &&
            user.getDescription().equals(userSub) &&
            user.getDob().equals(LocalDate.now())
        ));
    }

    @Test
    void shouldFindNearFriendsSuccessfully() {
        // Arrange
        GeoJsonPoint userLocation = new GeoJsonPoint(-122.4194, 37.7749); // San Francisco
        Address userAddress = new Address("User Address", userLocation);
        User user = new User("TestUser", LocalDate.now(), userAddress, "Test Description");
        user.setId("user1");

        // Create near users
        User nearUser1 = new User("NearUser1", LocalDate.now(), new Address("Near Address 1"), "Near User 1");
        nearUser1.setId("nearuser1");
        User nearUser2 = new User("NearUser2", LocalDate.now(), new Address("Near Address 2"), "Near User 2");
        nearUser2.setId("nearuser2");

        List<User> nearUsers = Arrays.asList(nearUser1, nearUser2);
        Distance distance = new Distance(5, Metrics.KILOMETERS);

        // Create follows (mutual friendship)
        List<Follow> mutualFollows1 = Arrays.asList(
            new Follow(user, nearUser1),
            new Follow(nearUser1, user)
        );
        List<Follow> oneWayFollows2 = Arrays.asList(
            new Follow(user, nearUser2)
        );

        given(userRepository.findByAddress_LocationNear(userLocation, distance)).willReturn(nearUsers);
        given(followRepository.friendFollows("user1", "nearuser1")).willReturn(mutualFollows1);
        given(followRepository.friendFollows("user1", "nearuser2")).willReturn(oneWayFollows2);

        // Act
        List<User> nearFriends = userService.findByNearFriends(user, distance);

        // Assert
        assertThat(nearFriends).hasSize(1);
        assertThat(nearFriends.get(0)).isEqualTo(nearUser1);
        verify(userRepository).findByAddress_LocationNear(userLocation, distance);
        verify(followRepository).friendFollows("user1", "nearuser1");
        verify(followRepository).friendFollows("user1", "nearuser2");
    }

    @Test
    void shouldReturnEmptyListWhenNoNearFriendsFound() {
        // Arrange
        GeoJsonPoint userLocation = new GeoJsonPoint(-122.4194, 37.7749);
        Address userAddress = new Address("User Address", userLocation);
        User user = new User("TestUser", LocalDate.now(), userAddress, "Test Description");
        user.setId("user1");

        Distance distance = new Distance(5, Metrics.KILOMETERS);
        List<User> nearUsers = new ArrayList<>();

        given(userRepository.findByAddress_LocationNear(userLocation, distance)).willReturn(nearUsers);

        // Act
        List<User> nearFriends = userService.findByNearFriends(user, distance);

        // Assert
        assertThat(nearFriends).isEmpty();
        verify(userRepository).findByAddress_LocationNear(userLocation, distance);
    }

    @Test
    void shouldHandleNullAttributesInOAuth2User() {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", null);
        attributes.put("sub", null);

        given(oauth2User.getAttributes()).willReturn(attributes);
        given(userRepository.findByName(null)).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // Act
        userService.syncAuth0User(oauth2User);

        // Assert
        verify(userRepository).findByName(null);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldCreateUserWhenNameIsNull() {
        // Arrange
        User user = new User();
        user.setName(null);

        given(userRepository.findByName(null)).willReturn(Optional.empty());
        given(userRepository.save(user)).willReturn(user);

        // Act
        User result = userService.createUser(user);

        // Assert
        assertThat(result).isEqualTo(user);
        verify(userRepository).findByName(null);
        verify(userRepository).save(user);
    }

    @Test
    void shouldUpdateUserWithAllFields() {
        // Arrange
        String userId = "user123";
        User existingUser = new User("OldName", LocalDate.of(1990, 1, 1), new Address("Old Address"), "Old Description");
        existingUser.setId(userId);

        User updatedUser = new User("NewName", LocalDate.of(1995, 5, 15), new Address("New Address"), "New Description");
        updatedUser.setId(userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateUser(updatedUser);

        // Assert
        assertThat(result.getName()).isEqualTo("NewName");
        assertThat(result.getDob()).isEqualTo(LocalDate.of(1995, 5, 15));
        assertThat(result.getDescription()).isEqualTo("New Description");
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }

}
