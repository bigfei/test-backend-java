package com.wiredcraft.wcapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "follows")
public class Follow {
    @Id
    private String id;

    @DBRef
    private User followee;
    @DBRef
    private User follower;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public Follow() {
    }

    public Follow(User followee, User follower) {
        this.followee = followee;
        this.follower = follower;
    }

    public User getFollowee() {
        return followee;
    }

    public User getFollower() {
        return follower;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow follow = (Follow) o;
        return Objects.equals(followee, follow.followee) && Objects.equals(follower, follow.follower);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followee, follower);
    }
}
