package com.backendoori.ootw.post.dto;

import java.time.LocalDateTime;
import com.backendoori.ootw.post.domain.Post;

public record PostReadResponse(
    Long postId,
    WriterDto writer,
    String title,
    String content,
    String image,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    WeatherDto weather
) {

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
