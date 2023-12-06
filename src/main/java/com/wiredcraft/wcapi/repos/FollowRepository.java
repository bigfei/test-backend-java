package com.wiredcraft.wcapi.repos;

import com.wiredcraft.wcapi.model.Follow;
import com.wiredcraft.wcapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends MongoRepository<Follow, String> {
    /**
     * Find all followers of a user
     * @param followee the user to be followed
     * @return a list of followers
     */
    List<Follow> findByFollowee(User followee);
    /**
     * Find all followees of a user
     * @param follower the user who follows
     * @return a list of followees
     */
    List<Follow> findByFollower(User follower);

    /**
     * Find a follow by followee and follower
     * @param followee the user to be followed
     * @param follower the user who follows
     * @return the follow
     */
    Optional<Follow> findFollowByFolloweeAndFollower(User followee, User follower);



    /**
     * Find all friends of a user
     * Friends are users who follow each other
     * @param userId0 the user id
     * @param userId1 the user id
     * @return a list of friends
     */
    @Query("{$or:[{$and: [{'followee.$id': ObjectId(?0)}, {'follower.$id': ObjectId(?1)}]}, " +
                 "{$and: [{'followee.$id': ObjectId(?1)}, {'follower.$id': ObjectId(?0)}]}]}"
    )
    List<Follow> friendFollows(String userId0, String userId1);
}
