package com.wiredcraft.wcapi.model;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class UserTest {

    @Test
    void shouldCreateUserWithDefaultConstructor() {
        User user = new User();
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isNull();
        assertThat(user.getDob()).isNull();
        assertThat(user.getAddress()).isNull();
        assertThat(user.getDescription()).isNull();
        assertThat(user.getCreatedAt()).isNull();
    }

    @Test
    void shouldCreateUserWithMainConstructor() {
        String name = "John Doe";
        LocalDate dob = LocalDate.of(1990, 1, 1);
        Address address = new Address("123 Main St");
        String description = "Test user";

        User user = new User(name, dob, address, description);

        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getDob()).isEqualTo(dob);
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getDescription()).isEqualTo(description);
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldCreateUserWithFullConstructor() {
        String name = "Jane Doe";
        LocalDate dob = LocalDate.of(1995, 5, 15);
        Address address = new Address("456 Oak Ave");
        String description = "Another test user";
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 10, 30);

        User user = new User(name, dob, address, description, createdAt);

        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getDob()).isEqualTo(dob);
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getDescription()).isEqualTo(description);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldSetAndGetId() {
        User user = new User();
        String id = "12345";
        user.setId(id);
        assertThat(user.getId()).isEqualTo(id);
    }

    @Test
    void shouldSetAndGetName() {
        User user = new User();
        String name = "Test Name";
        user.setName(name);
        assertThat(user.getName()).isEqualTo(name);
    }

    @Test
    void shouldSetAndGetDob() {
        User user = new User();
        LocalDate dob = LocalDate.of(1985, 3, 20);
        user.setDob(dob);
        assertThat(user.getDob()).isEqualTo(dob);
    }

    @Test
    void shouldSetAndGetAddress() {
        User user = new User();
        Address address = new Address("789 Pine St");
        user.setAddress(address);
        assertThat(user.getAddress()).isEqualTo(address);
    }

    @Test
    void shouldSetAndGetDescription() {
        User user = new User();
        String description = "Updated description";
        user.setDescription(description);
        assertThat(user.getDescription()).isEqualTo(description);
    }

    @Test
    void shouldSetAndGetCreatedAt() {
        User user = new User();
        LocalDateTime createdAt = LocalDateTime.now();
        user.setCreatedAt(createdAt);
        assertThat(user.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldTestEqualsAndHashCode() {
        User user1 = new User("John", LocalDate.now(), new Address("Addr1"), "Desc1");
        user1.setId("1");

        User user2 = new User("John", LocalDate.now(), new Address("Addr2"), "Desc2");
        user2.setId("1");

        User user3 = new User("Jane", LocalDate.now(), new Address("Addr1"), "Desc1");
        user3.setId("2");

        // Test equals
        assertTrue(user1.equals(user2)); // Same id and name
        assertFalse(user1.equals(user3)); // Different id and name
        assertFalse(user1.equals(null));
        assertFalse(user1.equals("string"));
        assertTrue(user1.equals(user1)); // Same object

        // Test hashCode
        assertEquals(user1.hashCode(), user2.hashCode()); // Same id and name should have same hash
        assertNotEquals(user1.hashCode(), user3.hashCode()); // Different id and name should have different hash
    }

    @Test
    void shouldTestEqualsWithNullValues() {
        User user1 = new User();
        User user2 = new User();

        assertTrue(user1.equals(user2)); // Both have null id and name

        user1.setId("1");
        assertFalse(user1.equals(user2)); // Different id

        user2.setId("1");
        assertTrue(user1.equals(user2)); // Same id, both null names

        user1.setName("John");
        assertFalse(user1.equals(user2)); // Different names

        user2.setName("John");
        assertTrue(user1.equals(user2)); // Same id and name
    }
}
