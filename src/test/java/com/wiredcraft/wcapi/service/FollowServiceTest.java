package com.wiredcraft.wcapi.service;

import com.wiredcraft.wcapi.model.Address;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class FollowServiceTest {
    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private FollowServiceImpl followService;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user1 = new User("Alice", LocalDate.of(1990, 1, 1), new Address("Address1"), "User1");
        user1.setId("user1");

        user2 = new User("Bob", LocalDate.of(1985, 5, 15), new Address("Address2"), "User2");
        user2.setId("user2");

        user3 = new User("Charlie", LocalDate.of(1992, 8, 20), new Address("Address3"), "User3");
        user3.setId("user3");
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
    void shouldNotCreateFollowWhenAlreadyFollowing() {
        final Follow existingFollow = new Follow(user1, user2);
        given(followRepository.findFollowByFolloweeAndFollower(user1, user2)).willReturn(Optional.of(existingFollow));

        boolean res = followService.follow(user1, user2);

        assertThat(res).isFalse();
        verify(followRepository, never()).save(any(Follow.class));
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

    @Test
    void shouldNotUnfollowWhenNotFollowing() {
        given(followRepository.findFollowByFolloweeAndFollower(user1, user2)).willReturn(Optional.empty());

        boolean res = followService.unfollow(user1, user2);

        assertThat(res).isFalse();
        verify(followRepository, never()).delete(any(Follow.class));
    }

    @Test
    void shouldFindFollowersByFollowee() {
        // user2 and user3 follow user1
        List<Follow> follows = Arrays.asList(
            new Follow(user1, user2),
            new Follow(user1, user3)
        );

        given(followRepository.findByFollowee(user1)).willReturn(follows);

        List<User> followers = followService.findFollowersByFollowee(user1);

        assertThat(followers).hasSize(2);
        assertThat(followers).containsExactly(user2, user3);
        verify(followRepository).findByFollowee(user1);
    }

    @Test
    void shouldReturnEmptyListWhenNoFollowers() {
        given(followRepository.findByFollowee(user1)).willReturn(Collections.emptyList());

        List<User> followers = followService.findFollowersByFollowee(user1);

        assertThat(followers).isEmpty();
        verify(followRepository).findByFollowee(user1);
    }

    @Test
    void shouldFindFolloweesByFollower() {
        // user1 follows user2 and user3
        List<Follow> follows = Arrays.asList(
            new Follow(user2, user1),
            new Follow(user3, user1)
        );

        given(followRepository.findByFollower(user1)).willReturn(follows);

        List<User> followees = followService.findFolloweesByFollower(user1);

        assertThat(followees).hasSize(2);
        assertThat(followees).containsExactly(user2, user3);
        verify(followRepository).findByFollower(user1);
    }

    @Test
    void shouldReturnEmptyListWhenNoFollowees() {
        given(followRepository.findByFollower(user1)).willReturn(Collections.emptyList());

        List<User> followees = followService.findFolloweesByFollower(user1);

        assertThat(followees).isEmpty();
        verify(followRepository).findByFollower(user1);
    }

    @Test
    void shouldHandleNullUsersInFollow() {
        User nullUser = null;
        given(followRepository.findFollowByFolloweeAndFollower(nullUser, user1)).willReturn(Optional.empty());

        boolean res = followService.follow(nullUser, user1);

        assertThat(res).isTrue();
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    void shouldHandleNullUsersInUnfollow() {
        User nullUser = null;
        given(followRepository.findFollowByFolloweeAndFollower(nullUser, user1)).willReturn(Optional.empty());

        boolean res = followService.unfollow(nullUser, user1);

        assertThat(res).isFalse();
        verify(followRepository, never()).delete(any(Follow.class));
    }

    @Test
    void shouldCreateFollowWithSameUserFollowingSelf() {
        given(followRepository.findFollowByFolloweeAndFollower(user1, user1)).willReturn(Optional.empty());

        boolean res = followService.follow(user1, user1);

        assertThat(res).isTrue();
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    void shouldReturnSingleFollowerWhenOnlyOneFollows() {
        List<Follow> follows = Arrays.asList(
            new Follow(user1, user2)
        );

        given(followRepository.findByFollowee(user1)).willReturn(follows);

        List<User> followers = followService.findFollowersByFollowee(user1);

        assertThat(followers).hasSize(1);
        assertThat(followers.get(0)).isEqualTo(user2);
    }

    @Test
    void shouldReturnSingleFolloweeWhenOnlyOneIsFollowed() {
        List<Follow> follows = Arrays.asList(
            new Follow(user2, user1)
        );

        given(followRepository.findByFollower(user1)).willReturn(follows);

        List<User> followees = followService.findFolloweesByFollower(user1);

        assertThat(followees).hasSize(1);
        assertThat(followees.get(0)).isEqualTo(user2);
    }

}
