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

    @Override
    public List<User> findFollowersByFollowee(User followee) {
        List<Follow> follows = followRepository.findByFollowee(followee);
        return follows.stream().map(Follow::getFollower).collect(Collectors.toList());
    }

    @Override
    public List<User> findFolloweesByFollower(User follower) {
        List<Follow> follows = followRepository.findByFollower(follower);
        return follows.stream().map(Follow::getFollowee).collect(Collectors.toList());
    }

    @Override
    public boolean follows(User src, User target) {
        Optional<Follow> followOptional = followRepository.findFollowByFolloweeAndFollower(src, target);
        if (followOptional.isEmpty()) {
            Follow follow = new Follow(src, target);
            followRepository.save(follow);
            return true;
        }
        return false;
    }

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
