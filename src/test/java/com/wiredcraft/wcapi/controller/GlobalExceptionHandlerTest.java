package com.wiredcraft.wcapi.controller;

import com.wiredcraft.wcapi.exception.UserRegistrationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void shouldHandleUserRegistrationException() {
        // Arrange
        String errorMessage = "User with name 'John Doe' already exists";
        UserRegistrationException exception = new UserRegistrationException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleCustomException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("error")).isEqualTo(errorMessage);
        assertThat(body.get("status")).isEqualTo("400");
    }

    @Test
    void shouldHandleUserRegistrationExceptionWithDifferentMessage() {
        // Arrange
        String errorMessage = "Email validation failed";
        UserRegistrationException exception = new UserRegistrationException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleCustomException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        if (body != null) {
            assertThat(body.get("error")).isEqualTo(errorMessage);
            assertThat(body.get("status")).isEqualTo("400");
            assertThat(body).hasSize(2);
        }
    }

    @Test
    void shouldHandleUserRegistrationExceptionWithNullMessage() {
        // Arrange
        UserRegistrationException exception = new UserRegistrationException(null);

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleCustomException(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, String> body = response.getBody();
        assertThat(body).isNotNull();
        if (body != null) {
            assertThat(body.get("error")).isNull();
            assertThat(body.get("status")).isEqualTo("400");
        }
    }
}
