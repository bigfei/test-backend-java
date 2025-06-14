package com.wiredcraft.wcapi.exception;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
public class UserRegistrationExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String message = "User registration failed";
        UserRegistrationException exception = new UserRegistrationException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldCreateExceptionWithNullMessage() {
        UserRegistrationException exception = new UserRegistrationException(null);

        assertThat(exception.getMessage()).isNull();
    }

    @Test
    void shouldCreateExceptionWithEmptyMessage() {
        String emptyMessage = "";
        UserRegistrationException exception = new UserRegistrationException(emptyMessage);

        assertThat(exception.getMessage()).isEqualTo(emptyMessage);
    }

    @Test
    void shouldBeThrowable() {
        String message = "User already exists";

        assertThrows(UserRegistrationException.class, () -> {
            throw new UserRegistrationException(message);
        });
    }

    @Test
    void shouldPreserveStackTrace() {
        UserRegistrationException exception = new UserRegistrationException("Test message");
        StackTraceElement[] stackTrace = exception.getStackTrace();

        assertThat(stackTrace).isNotNull();
        assertThat(stackTrace.length).isGreaterThan(0);
    }

    @Test
    void shouldCreateExceptionWithSpecificUserName() {
        String userName = "john.doe@example.com";
        String expectedMessage = "User registration failed for: " + userName;
        UserRegistrationException exception = new UserRegistrationException(expectedMessage);

        assertThat(exception.getMessage()).contains(userName);
        assertThat(exception.getMessage()).startsWith("User registration failed for:");
    }
}
