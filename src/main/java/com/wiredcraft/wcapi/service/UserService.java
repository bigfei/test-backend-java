package com.wiredcraft.wcapi.service;

import com.wiredcraft.wcapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);

    Optional<User> getUserById(String userId);

    Page<User> getAllUsers(Pageable paging);

    User updateUser(User user);

    void deleteUser(String userId);

    void syncAuth0User(OAuth2User user);

    List<User> findByNearFriends(User u, Distance distance);
}
