package com.wiredcraft.wcapi.repos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiredcraft.wcapi.model.Follow;
import com.wiredcraft.wcapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.BulkOperations.BulkMode;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class FollowRepositoryTest {

    public static final String COL_USER_NAME = "users";
    public static final String COL_FOLLOW_NAME = "follows";
    public static final String DATA_PATH = "/mongo/01_users.json";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ObjectMapper mapper;
    private List<User> us;

    @BeforeEach
    public void setup() throws Exception {
        mongoTemplate.bulkOps(BulkMode.UNORDERED, Follow.class, COL_FOLLOW_NAME).remove(new Query()).execute();
        mongoTemplate.bulkOps(BulkMode.UNORDERED, User.class, COL_USER_NAME).remove(new Query()).execute();
        List<User> users = Arrays.asList(mapper.readValue(new ClassPathResource(DATA_PATH).getFile(), User[].class));
        mongoTemplate.bulkOps(BulkMode.UNORDERED, User.class, COL_USER_NAME).insert(users).execute();

        us = mongoTemplate.findAll(User.class, COL_USER_NAME);
        List<Follow> follows = new ArrayList<>();
        for (int i = 0; i < us.size(); i++) {
            User follower = us.get(i);
            for (User followee : us) {
                if (!follower.equals(followee)) {
                    Follow follow = new Follow(follower, followee);
                    follows.add(follow);
                }
            }
        }
        mongoTemplate.bulkOps(BulkMode.UNORDERED, Follow.class, COL_FOLLOW_NAME).insert(follows).execute();
    }

    @Test
    public void testFindByFollowee() {
        User u = us.get(0);
        List<Follow> fs = followRepository.findByFollowee(u);
        assertEquals(9, fs.size());
    }

    @Test
    public void testFindByFollower() {
        User u = us.get(1);
        List<Follow> fs = followRepository.findByFollower(u);
        assertEquals(9, fs.size());
    }

    @Test
    public void testFriendFollows() {
        User u0 = us.get(0);
        User u1 =us.get(1);
        List<Follow> fs = followRepository.friendFollows(u0.getId(), u1.getId());
        assertEquals(2, fs.size());
    }

    @Test
    public void testFindFollowByFolloweeAndFollower() {
        User u0 = us.get(0);
        User u1 =us.get(1);
        Optional<Follow> fs = followRepository.findFollowByFolloweeAndFollower(u0, u1);
        assertNotNull(fs);
        assertTrue(fs.isPresent());
    }

}
