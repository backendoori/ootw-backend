package com.backendoori.ootw.post.dto.response;

import java.time.LocalDateTime;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.weather.dto.TemperatureArrangeDto;

public record PostSaveUpdateResponse(
    Long postId,
    String title,
    String content,
    String image,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    TemperatureArrangeDto temperatureArrange
) {

    public static PostSaveUpdateResponse from(Post savedPost) {
        return new PostSaveUpdateResponse(
            savedPost.getId(),
            savedPost.getTitle(),
            savedPost.getContent(),
            savedPost.getImageUrl(),
            savedPost.getCreatedAt(),
            savedPost.getUpdatedAt(),
            TemperatureArrangeDto.from(savedPost.getTemperatureArrange())
        );
    }

}
