package com.backendoori.ootw.dto;

import java.time.LocalDateTime;
import com.backendoori.ootw.domain.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostSaveResponse {

    private final Long postId;
    private final String title;
    private final String content;
    private final String image;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final WeatherInfo weather;

    public static PostSaveResponse from(Post savedPost) {
        return new PostSaveResponse(
            savedPost.getId(),
            savedPost.getTitle(),
            savedPost.getContent(),
            savedPost.getImage(),
            savedPost.getCreatedAt(),
            savedPost.getUpdatedAt(),
            WeatherInfo.from(savedPost.getWeather())
        );
    }

}
