package com.backendoori.ootw.post.dto;

import java.time.LocalDateTime;
import com.backendoori.ootw.post.domain.Post;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostReadResponse {

    private final Long postId;
    private final WriterDto writer;
    private final String title;
    private final String content;
    private final String image;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final WeatherDto weather;

    public static PostReadResponse from(Post post) {
        return new PostReadResponse(
            post.getId(),
            WriterDto.from(post.getUser()),
            post.getTitle(),
            post.getContent(),
            post.getImage(),
            post.getCreatedAt(),
            post.getUpdatedAt(),
            WeatherDto.from(post.getWeather())
        );
    }

}
