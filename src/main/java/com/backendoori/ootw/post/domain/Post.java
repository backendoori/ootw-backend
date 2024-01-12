package com.backendoori.ootw.post.domain;

import static com.backendoori.ootw.post.validation.PostValidator.validateContent;
import static com.backendoori.ootw.post.validation.PostValidator.validatePostSaveRequest;
import static com.backendoori.ootw.post.validation.PostValidator.validateTemperatureArrange;
import static com.backendoori.ootw.post.validation.PostValidator.validateTitle;
import static com.backendoori.ootw.post.validation.PostValidator.validateUser;

import com.backendoori.ootw.common.BaseEntity;
import com.backendoori.ootw.post.dto.request.PostSaveRequest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
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

    @Column(name = "image_url")
    private String imageUrl;

    @Embedded
    private TemperatureArrange temperatureArrange;

    @Column(name = "like_cnt")
    private int likeCnt;

    private Post(User user, PostSaveRequest request, String imgUrl, TemperatureArrange temperatureArrange) {
        validateUser(user);
        validatePostSaveRequest(request);
        validateTemperatureArrange(temperatureArrange);

        this.user = user;
        this.title = request.title();
        this.content = request.content();
        this.imageUrl = imgUrl;
        this.temperatureArrange = temperatureArrange;
    }

    public static Post from(User user, PostSaveRequest request, String imgUrl, TemperatureArrange temperatureArrange) {
        return new Post(user, request, imgUrl, temperatureArrange);
    }

    public void increaseLikeCnt() {
        this.likeCnt++;
    }

    public void decreaseLikeCnt() {
        this.likeCnt--;
    }

    public void updateTitle(String title) {
        validateTitle(title);
        this.title = title;
    }

    public void updateContent(String content) {
        validateContent(content);
        this.content = content;
    }

    public void updateImageUrl(String imageUrl) {
        validateContent(content);
        this.imageUrl = imageUrl;
    }

}
