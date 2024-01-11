package com.backendoori.ootw.post.dto.response;

import java.time.LocalDateTime;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.weather.dto.TemperatureArrangeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class PostReadResponse {

    private final Long postId;
    private final WriterDto writer;
    private final String title;
    private final String content;
    private final String image;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final TemperatureArrangeDto temperatureArrange;
    private final int likeCnt;
    private int isLike;

    public static PostReadResponse from(Post post) {
        return new PostReadResponse(
            post.getId(),
            WriterDto.from(post.getUser()),
            post.getTitle(),
            post.getContent(),
            post.getImage(),
            post.getCreatedAt(),
            post.getUpdatedAt(),
            TemperatureArrangeDto.from(post.getTemperatureArrange()),
            post.getLikeCnt(),
            0
            );
    }

    public void updateIsLike() {
        this.isLike = 1;
    }

}
