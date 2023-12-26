package com.backendoori.ootw.dto;

import java.time.LocalDateTime;
import com.backendoori.ootw.domain.Post;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostDetailInfo {

    private final Long postId;
    private final WriterDto writer;
    private final String title;
    private final String content;
    private final String image;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final WeatherDto weather;

    public static PostDetailInfo from(Post post) {
        return new PostDetailInfo(
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
