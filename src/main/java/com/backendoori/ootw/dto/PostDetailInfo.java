package com.backendoori.ootw.dto;

import java.time.LocalDateTime;
import com.backendoori.ootw.domain.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostDetailInfo {

    private final Long postId;
    private final PostWriterInfo writer;
    private final String title;
    private final String content;
    private final String image;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PostDetailInfo from(Post post) {
        return new PostDetailInfo(
            post.getId(),
            PostWriterInfo.from(post.getUser()),
            post.getTitle(),
            post.getContent(),
            post.getImage(),
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }

}
