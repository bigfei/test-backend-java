package com.wiredcraft.wcapi.service;

import com.wiredcraft.wcapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    User createUser(User user);

    Optional<User> getUserById(String userId);

    Page<User> getAllUsers(Pageable paging);

    User updateUser(User user);

    void deleteUser(String userId);
}
