package com.wiredcraft.wcapi.service;

import com.wiredcraft.wcapi.exception.UserRegistrationException;
import com.wiredcraft.wcapi.model.User;
import com.wiredcraft.wcapi.repos.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        Optional<User> userOptional = userRepository.findByName(user.getName());
        if(userOptional.isPresent()) {
            throw new UserRegistrationException("User with name "+ user.getName()+" already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Page<User> getAllUsers(Pageable paging) {
        return userRepository.findAll(paging);
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).get();
        existingUser.setAddress(user.getAddress());
        existingUser.setDescription(user.getDescription());
        existingUser.setDob(user.getDob());
        existingUser.setName(user.getName());
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void syncAuth0User(OAuth2User user) {
        String name = (String)user.getAttributes().get("name");
        Optional<User> userOptional = userRepository.findByName(user.getName());
        if(userOptional.isPresent()) {
            //syncUser from auth0
            User localUser = userOptional.get();
            localUser.setDescription((String)user.getAttributes().get("sub"));
            userRepository.save(localUser);
        }else{
            //createUser from auth0 into localdb
            User u = new User(name, LocalDate.now(), "Addr1", (String)user.getAttributes().get("sub"));
            userRepository.save(u);
        }
    }
}