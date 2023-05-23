package com.wiredcraft.wcapi.controller;

import com.wiredcraft.wcapi.model.User;
import com.wiredcraft.wcapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal OAuth2User user) {
        if (user == null) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            return ResponseEntity.ok().body(user.getAttributes());
        }
    }

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
        return new ModelAndView("profile" , Collections.singletonMap("details", authentication.getPrincipal().getAttributes()));
    }

}
