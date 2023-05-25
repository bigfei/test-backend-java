package com.wiredcraft.wcapi.repos;

import com.wiredcraft.wcapi.model.Follow;
import com.wiredcraft.wcapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends MongoRepository<Follow, String> {
    List<Follow> findByFollowee(User followee);
    List<Follow> findByFollower(User follower);

    Optional<Follow> findFollowByFolloweeAndFollower(User followee, User follower);

    //@Query("{$or: [{'followee' :{'$ref' : 'users' , '$id' : ObjectId(?0)}}, {'follower' :{'$ref' : 'users' , '$id' : ObjectId(?1)}}]}")
    @Query("{$or:[{$and: [{'followee' :{'$ref' : 'users' , '$id' : ObjectId(?0)}}, {'follower' :{'$ref' : 'users' , '$id' : ObjectId(?1)}}]}" +
            ",{$and: [{'followee' :{'$ref' : 'users' , '$id' : ObjectId(?1)}}, {'follower' :{'$ref' : 'users' , '$id' : ObjectId(?0)}}]}" +
            "]}")
    List<Follow> friendFollows(String userId0, String userId1);
}
//{ followee: { $ref: "users", $id: ObjectId("646f41d237aca45002d21d58") } }
