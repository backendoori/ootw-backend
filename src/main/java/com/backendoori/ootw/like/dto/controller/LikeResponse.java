package com.backendoori.ootw.like.dto.controller;

import com.backendoori.ootw.like.domain.Like;

public record LikeResponse(
    Long likeId,
    Long userId,
    Long postId,
    boolean status
    ) {

    public static LikeResponse from(Like like) {
        return new LikeResponse(
            like.getId(),
            like.getUser().getId(),
            like.getPost().getId(),
            like.getStatus());
    }

}
