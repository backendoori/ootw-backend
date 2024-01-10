package com.backendoori.ootw.post.dto;

import java.time.LocalDateTime;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.weather.dto.TemperatureArrangeDto;

public record PostSaveResponse(
    Long postId,
    String title,
    String content,
    String image,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    TemperatureArrangeDto temperatureArrange
) {

    public static PostSaveResponse from(Post savedPost) {
        return new PostSaveResponse(
            savedPost.getId(),
            savedPost.getTitle(),
            savedPost.getContent(),
            savedPost.getImage(),
            savedPost.getCreatedAt(),
            savedPost.getUpdatedAt(),
            TemperatureArrangeDto.from(savedPost.getTemperatureArrange())
        );
    }

}
