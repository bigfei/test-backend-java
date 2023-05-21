package com.wiredcraft.wcapi.repos;

import com.wiredcraft.wcapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByName(String name);
    Page<User> findByName(String name, Pageable pageable);

    User deleteByName(String name);
}
