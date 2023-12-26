package com.backendoori.ootw.domain;

import java.util.Objects;
import com.backendoori.ootw.domain.weather.Weather;
import com.backendoori.ootw.dto.PostSaveRequest;
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

@Table(name = "posts")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

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

    private static void validateUser(User user) {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("게시글 생성 요청 사용자가 null이어서는 안됩니다.");
        }
    }

    // TODO: Validator 클래스를 독립적으로 만드는 것이 나을까..?
    private static void validatePostSaveRequest(PostSaveRequest request) {
        if (Objects.isNull(request)
            || Objects.isNull(request.title())
            || Objects.isNull(request.content())
            || Objects.isNull(request.weather())) {
            throw new IllegalArgumentException("게시글 생성 요청 정보가 null이어서는 안됩니다.");
        }
    }

}
