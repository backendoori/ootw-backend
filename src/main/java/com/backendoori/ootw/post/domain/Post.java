package com.backendoori.ootw.post.domain;

import java.util.Objects;
import com.backendoori.ootw.common.BaseEntity;
import com.backendoori.ootw.post.domain.weather.Weather;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Table(name = "posts")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    private static final Integer MAX_TITLE_LENGTH = 30;
    private static final Integer MAX_CONTENT_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "image")
    private String image;

    @Embedded
    private Weather weather;

    private Post(User user, PostSaveRequest request) {
        validateUser(user);
        validatePostSaveRequest(request);
        this.user = user;
        this.title = request.title();
        this.content = request.content();
        this.image = request.image();
        this.weather = Weather.from(request.weather());
    }

    public static Post from(User user, PostSaveRequest request) {
        return new Post(user, request);
    }

    // TODO: Validator 클래스를 독립적으로 만드는 것이 나을까..?
    private static void validateUser(User user) {
        Assert.isTrue(!Objects.isNull(user), "게시글 생성 요청 사용자가 null이어서는 안됩니다.");
    }

    private static void validatePostSaveRequest(PostSaveRequest request) {
        Assert.isTrue(!Objects.isNull(request), "게시글 생성 요청 정보가 null이어서는 안됩니다.");
        Assert.isTrue(!Objects.isNull(request.userId()), "게시글 작성자 ID가 null이어서는 안됩니다.");
        Assert.isTrue(!Objects.isNull(request.weather()), "게시글 기온/날씨 정보가 null이어서는 안됩니다.");
        validateTitle(request.title());
        validateContent(request.content());
    }

    private static void validateTitle(String title) {
        Assert.isTrue(!Objects.isNull(title), "게시글 제목이 null이어서는 안됩니다.");
        Assert.isTrue(!title.isBlank(), "게시글 제목이 공백이어서는 안됩니다.");
        Assert.isTrue(!(title.length() > MAX_TITLE_LENGTH), "게시글 제목은 30자 이내여야 합니다.");
    }

    private static void validateContent(String content) {
        Assert.isTrue(!Objects.isNull(content), "게시글 내용이 null이어서는 안됩니다.");
        Assert.isTrue(!content.isBlank(), "게시글 내용이 공백이어서는 안됩니다.");
        Assert.isTrue(!(content.length() > MAX_CONTENT_LENGTH), "게시글 내용은 500자 이내여야 합니다.");
    }

}
