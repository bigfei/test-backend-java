package com.wiredcraft.wcapi.model;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class FollowTest {

    @Test
    void shouldCreateFollowWithDefaultConstructor() {
        Follow follow = new Follow();

        assertThat(follow.getFollowee()).isNull();
        assertThat(follow.getFollower()).isNull();
        assertThat(follow.getCreatedAt()).isNull();
    }

    @Test
    void shouldCreateFollowWithParameterizedConstructor() {
        User followee = new User("Alice", LocalDate.of(1990, 1, 1), new Address("Address1"), "User1");
        User follower = new User("Bob", LocalDate.of(1985, 5, 15), new Address("Address2"), "User2");

        Follow follow = new Follow(followee, follower);

        assertThat(follow.getFollowee()).isEqualTo(followee);
        assertThat(follow.getFollower()).isEqualTo(follower);
        assertThat(follow.getCreatedAt()).isNull(); // CreatedAt is managed by @CreatedDate annotation
    }

    @Test
    void shouldSetAndGetCreatedAt() {
        Follow follow = new Follow();
        LocalDateTime createdAt = LocalDateTime.now();

        follow.setCreatedAt(createdAt);

        assertThat(follow.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldTestEqualsAndHashCode() {
        User followee1 = new User("Alice", LocalDate.now(), new Address("Addr1"), "Desc1");
        User follower1 = new User("Bob", LocalDate.now(), new Address("Addr2"), "Desc2");

        User followee2 = new User("Alice", LocalDate.now(), new Address("Addr1"), "Desc1");
        User follower2 = new User("Bob", LocalDate.now(), new Address("Addr2"), "Desc2");

        User followee3 = new User("Charlie", LocalDate.now(), new Address("Addr3"), "Desc3");
        User follower3 = new User("David", LocalDate.now(), new Address("Addr4"), "Desc4");

        Follow follow1 = new Follow(followee1, follower1);
        Follow follow2 = new Follow(followee2, follower2);
        Follow follow3 = new Follow(followee3, follower3);

        // Test equals
        assertTrue(follow1.equals(follow2)); // Same followee and follower
        assertFalse(follow1.equals(follow3)); // Different followee and follower
        assertFalse(follow1.equals(null));
        assertFalse(follow1.equals("string"));
        assertTrue(follow1.equals(follow1)); // Same object

        // Test hashCode
        assertEquals(follow1.hashCode(), follow2.hashCode()); // Same followee and follower should have same hash
        assertNotEquals(follow1.hashCode(), follow3.hashCode()); // Different followee and follower should have different hash
    }

    @Test
    void shouldTestEqualsWithNullFolloweeAndFollower() {
        Follow follow1 = new Follow(null, null);
        Follow follow2 = new Follow(null, null);

        assertTrue(follow1.equals(follow2)); // Both have null followee and follower

        User user = new User("Test", LocalDate.now(), new Address("Addr"), "Desc");
        Follow follow3 = new Follow(user, null);
        Follow follow4 = new Follow(user, null);

        assertTrue(follow3.equals(follow4)); // Same followee, both null followers
        assertFalse(follow1.equals(follow3)); // Different followee
    }

    @Test
    void shouldTestEqualsWithPartialNullValues() {
        User followee = new User("Alice", LocalDate.now(), new Address("Addr1"), "Desc1");
        User follower = new User("Bob", LocalDate.now(), new Address("Addr2"), "Desc2");

        Follow follow1 = new Follow(followee, null);
        Follow follow2 = new Follow(followee, null);
        Follow follow3 = new Follow(null, follower);
        Follow follow4 = new Follow(null, follower);

        assertTrue(follow1.equals(follow2)); // Same followee, both null followers
        assertTrue(follow3.equals(follow4)); // Same follower, both null followees
        assertFalse(follow1.equals(follow3)); // Different combinations
    }

    @Test
    void shouldCreateFollowRelationship() {
        User alice = new User("Alice", LocalDate.of(1990, 1, 1), new Address("Alice Street"), "Alice's profile");
        User bob = new User("Bob", LocalDate.of(1985, 12, 25), new Address("Bob Avenue"), "Bob's profile");

        // Bob follows Alice
        Follow follow = new Follow(alice, bob);

        assertThat(follow.getFollowee()).isEqualTo(alice);
        assertThat(follow.getFollower()).isEqualTo(bob);
        assertThat(follow.getFollowee().getName()).isEqualTo("Alice");
        assertThat(follow.getFollower().getName()).isEqualTo("Bob");
    }
}
