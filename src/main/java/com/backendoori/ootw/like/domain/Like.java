package com.backendoori.ootw.like.domain;

import com.backendoori.ootw.common.BaseEntity;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.user.domain.User;
import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "likes")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "is_like", columnDefinition = "tinyint")
    private Boolean isLike;

    private Like(Long id, User user, Post post, Boolean status) {
        Assert.notNull(user);
        Assert.notNull(post);
        Assert.notNull(status);
        this.id = id;
        this.user = user;
        this.post = post;
        this.isLike = status;
    }

    public boolean updateStatus() {
        this.isLike = !isLike;
        return this.isLike;
    }

}
