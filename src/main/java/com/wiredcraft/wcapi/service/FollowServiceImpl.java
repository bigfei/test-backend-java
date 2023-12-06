package com.wiredcraft.wcapi.service;

import com.wiredcraft.wcapi.model.Follow;
import com.wiredcraft.wcapi.model.User;
import com.wiredcraft.wcapi.repos.FollowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FollowServiceImpl implements FollowService {
    private FollowRepository followRepository;

    public FollowServiceImpl(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    /**
     * Find all followers of a user
     * @param followee the user to be followed
     * @return a list of followers
     */
    @Override
    public List<User> findFollowersByFollowee(User followee) {
        List<Follow> follows = followRepository.findByFollowee(followee);
        return follows.stream().map(Follow::getFollower).collect(Collectors.toList());
    }

    /**
     * Find all followees of a user
     * @param follower the user who follows
     * @return a list of followees
     */
    @Override
    public List<User> findFolloweesByFollower(User follower) {
        List<Follow> follows = followRepository.findByFollower(follower);
        return follows.stream().map(Follow::getFollowee).collect(Collectors.toList());
    }

    /**
     * Follow a user
     * @param src the user who follows
     * @param target the user to be followed
     * @return true if the user is followed successfully, false for already followed or other errors
     */
    @Override
    public boolean follow(User src, User target) {
        Optional<Follow> followOptional = followRepository.findFollowByFolloweeAndFollower(src, target);
        if (followOptional.isEmpty()) {
            Follow follow = new Follow(src, target);
            followRepository.save(follow);
            return true;
        }
        return false;
    }

    /**
     * Unfollow a user
     * @param src the user who follows
     * @param target the user to be unfollowed
     * @return true if the user is unfollowed successfully, false for not followed or other errors
     */
    @Override
    public boolean unfollow(User src, User target) {
        Optional<Follow> followOptional = followRepository.findFollowByFolloweeAndFollower(src, target);
        if (followOptional.isPresent()) {
            followRepository.delete(followOptional.get());
            return true;
        }
        return false;
    }
}
