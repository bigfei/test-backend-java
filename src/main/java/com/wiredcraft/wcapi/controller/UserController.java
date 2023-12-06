package com.wiredcraft.wcapi.controller;

import com.wiredcraft.wcapi.model.User;
import com.wiredcraft.wcapi.service.FollowService;
import com.wiredcraft.wcapi.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    private FollowService followService;

    public UserController(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    /**
     * Get all users
     * @param page page number
     * @param size page size
     * @return list of users
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<User> pageUsers = userService.getAllUsers(paging);

        Map<String, Object> response = new HashMap<>();
        response.put("data", pageUsers.getContent());
        response.put("currentPage", pageUsers.getNumber());
        response.put("totalItems", pageUsers.getTotalElements());
        response.put("totalPages", pageUsers.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get a user by id
     * @param id user id
     * @return user
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get current user
     * @param user oauth2 user
     * @return user
     */
    @GetMapping("/me")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            return ResponseEntity.ok().body(user.getAttributes());
        }
    }

    /**
     * Update a user
     * @param userId user id
     * @param user user
     * @return user
     */
    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String userId,
                                           @RequestBody User user) {
        return userService.getUserById(userId)
                .map(userObj -> {
                    userObj.setId(userId);
                    return ResponseEntity.ok(userService.updateUser(userObj));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<User> deleteUser(@PathVariable("id") String userId) {
        return userService.getUserById(userId)
                .map(user -> {
                    userService.deleteUser(userId);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('SCOPE_profile')")
    public ModelAndView userDetails(OAuth2AuthenticationToken authentication) {
        return new ModelAndView("profile", Collections.singletonMap("details", authentication.getPrincipal().getAttributes()));
    }

    /**
     * Get followers of a user
     * @param userId user id
     * @return list of followers
     */
    @GetMapping("{id}/followers")
    public ResponseEntity<List<User>> followers(@PathVariable("id") String userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<User> followers = followService.findFollowersByFollowee(user.get());
            return new ResponseEntity<>(followers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get followees of a user
     * @param userId user id
     * @return list of followees
     */
    @GetMapping("{id}/following")
    public ResponseEntity<List<User>> following(@PathVariable("id") String userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<User> followees = followService.findFolloweesByFollower(user.get());
            return new ResponseEntity<>(followees, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Unfollow a user
     * @param userId user id
     * @param targetUserId target user id
     * @return success or not
     */
    @DeleteMapping("{id}/following/{target}")
    public ResponseEntity<?> unfollow(@PathVariable("id") String userId, @PathVariable("target") String targetUserId) {
        Optional<User> src = userService.getUserById(userId);
        Optional<User> target = userService.getUserById(targetUserId);
        if (src.isPresent() && target.isPresent()) {
            boolean res = followService.unfollow(src.get(), target.get());
            if (res) {
                return ResponseEntity.ok().body("success");
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Follow a user
     * @param userId user id
     * @param targetUserId  target user id
     * @return success or not
     */
    @PostMapping("{id}/following/{target}")
    public ResponseEntity<?> follow(@PathVariable("id") String userId, @PathVariable("target") String targetUserId) {
        Optional<User> src = userService.getUserById(userId);
        Optional<User> target = userService.getUserById(targetUserId);
        if (src.isPresent() && target.isPresent()) {
            boolean res = followService.follow(src.get(), target.get());
            if (res) {
                return ResponseEntity.ok().body("success");
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     *  Get users who are within the distance of the user in km
     * @param userId user id
     * @param distanceKm distance in km
     * @return list of users who are within the distance of the user in km
     */
    @GetMapping("{id}/nearFriends")
    public ResponseEntity<List<User>> nearFriends(@PathVariable("id") String userId,
                                                  @RequestParam(defaultValue = "10") int distanceKm) {
        Distance distance = new Distance(distanceKm, Metrics.KILOMETERS);

        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<User> friends = userService.findByNearFriends(user.get(), distance);
            return new ResponseEntity<>(friends, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
