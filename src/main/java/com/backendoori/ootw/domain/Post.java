package com.backendoori.ootw.domain;

import com.backendoori.ootw.domain.weather.Weather;
import com.backendoori.ootw.dto.PostSaveRequest;
import com.backendoori.ootw.dto.WeatherInfo;
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

    private Post(User user, String title, String content, String image, WeatherInfo weather) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.image = image;
        this.weather = Weather.from(weather);
    }

    public static Post from(User user, PostSaveRequest request) {
        return new Post(user, request.title(), request.content(), request.image(), request.weather());
    }

}
