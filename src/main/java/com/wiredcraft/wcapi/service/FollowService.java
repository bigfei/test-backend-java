package com.wiredcraft.wcapi.service;

import com.wiredcraft.wcapi.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FollowService {
    List<User> findFollowersByFollowee(User followee);
    List<User> findFolloweesByFollower(User follower);

    boolean follows(User src, User target);
    boolean unfollow(User src, User target);
}
