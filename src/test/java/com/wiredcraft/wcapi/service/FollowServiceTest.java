package com.wiredcraft.wcapi.service;

import com.wiredcraft.wcapi.model.Follow;
import com.wiredcraft.wcapi.model.User;
import com.wiredcraft.wcapi.repos.FollowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class FollowServiceTest {
    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private FollowServiceImpl followService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateFollow() {
        final User u0 = new User();
        final User u1 = new User();
        final Follow follow = new Follow(u0, u1);
        given(followRepository.findFollowByFolloweeAndFollower(u0, u1)).willReturn(Optional.empty());
        given(followRepository.save(follow)).willAnswer(invocation -> invocation.getArgument(0));

        boolean res = followService.follow(u0, u1);
        assertThat(res).isTrue();
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    void shouldUnfollow() {
        final User u0 = new User();
        final User u1 = new User();

        final Follow follow = new Follow(u0, u1);
        given(followRepository.findFollowByFolloweeAndFollower(u0, u1)).willReturn(Optional.of(follow));

        boolean res = followService.unfollow(u0, u1);
        assertThat(res).isTrue();
        verify(followRepository).delete(any(Follow.class));
    }
}
