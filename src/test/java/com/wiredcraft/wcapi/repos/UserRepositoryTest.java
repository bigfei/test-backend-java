package com.wiredcraft.wcapi.repos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiredcraft.wcapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserRepositoryTest {

    public static final String COL_NAME = "users";
    public static final String DATA_PATH = "/mongo/01_users.json";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() throws Exception {
        mongoTemplate.bulkOps(BulkMode.UNORDERED, User.class, COL_NAME).remove(new Query()).execute();
        List<User> users = Arrays.asList(mapper.readValue(new ClassPathResource(DATA_PATH).getFile(), User[].class));
        mongoTemplate.bulkOps(BulkMode.UNORDERED, User.class, COL_NAME).insert(users).execute();
    }

    @DisplayName("Check that the users is retrieved by user name.")
    @Test
    public void testFindByUserName() {
        Pageable paging = PageRequest.of(0, 10);
        Page<User> actual = userRepository.findByName("David Johnson", paging);
        assertEquals(1, actual.getTotalElements());

        User u = actual.getContent().get(0);
        assertEquals(u.getDob(), LocalDate.parse("1992-12-01"));
    }

    @DisplayName("Check that the user can be deleted by user name.")
    @Test
    public void testDeleteByUserName() {
        Pageable paging = PageRequest.of(0, 10);
        Page<User> actual = userRepository.findByName("David Johnson", paging);
        assertEquals(1, actual.getTotalElements());

        User u = actual.getContent().get(0);
        String id = u.getId();
        User delUser = userRepository.deleteByName(u.getName());
        assertEquals(id, delUser.getId());
    }
}
