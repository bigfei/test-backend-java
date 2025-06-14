package com.wiredcraft.wcapi.model;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class AddressTest {

    @Test
    void shouldCreateAddressWithDefaultConstructor() {
        Address address = new Address();
        assertThat(address.getName()).isNull();
        assertThat(address.getLocation()).isNull();
    }

    @Test
    void shouldCreateAddressWithNameConstructor() {
        String name = "123 Main Street";
        Address address = new Address(name);

        assertThat(address.getName()).isEqualTo(name);
        assertThat(address.getLocation()).isNull();
    }

    @Test
    void shouldCreateAddressWithFullConstructor() {
        String name = "456 Oak Avenue";
        GeoJsonPoint location = new GeoJsonPoint(-73.935242, 40.730610); // New York coordinates

        Address address = new Address(name, location);

        assertThat(address.getName()).isEqualTo(name);
        assertThat(address.getLocation()).isEqualTo(location);
    }

    @Test
    void shouldSetAndGetName() {
        Address address = new Address();
        String name = "789 Pine Street";

        address.setName(name);

        assertThat(address.getName()).isEqualTo(name);
    }

    @Test
    void shouldSetAndGetLocation() {
        Address address = new Address();
        GeoJsonPoint location = new GeoJsonPoint(-122.4194, 37.7749); // San Francisco coordinates

        address.setLocation(location);

        assertThat(address.getLocation()).isEqualTo(location);
        assertThat(address.getLocation().getX()).isEqualTo(-122.4194);
        assertThat(address.getLocation().getY()).isEqualTo(37.7749);
    }

    @Test
    void shouldHandleNullValues() {
        Address address = new Address();

        address.setName(null);
        address.setLocation(null);

        assertThat(address.getName()).isNull();
        assertThat(address.getLocation()).isNull();
    }

    @Test
    void shouldCreateAddressWithEmptyName() {
        String emptyName = "";
        Address address = new Address(emptyName);

        assertThat(address.getName()).isEqualTo(emptyName);
        assertThat(address.getLocation()).isNull();
    }

    @Test
    void shouldUpdateExistingAddress() {
        Address address = new Address("Original Name");
        GeoJsonPoint originalLocation = new GeoJsonPoint(0.0, 0.0);
        address.setLocation(originalLocation);

        // Update name and location
        String newName = "Updated Name";
        GeoJsonPoint newLocation = new GeoJsonPoint(1.0, 1.0);

        address.setName(newName);
        address.setLocation(newLocation);

        assertThat(address.getName()).isEqualTo(newName);
        assertThat(address.getLocation()).isEqualTo(newLocation);
        assertThat(address.getLocation().getX()).isEqualTo(1.0);
        assertThat(address.getLocation().getY()).isEqualTo(1.0);
    }
}
