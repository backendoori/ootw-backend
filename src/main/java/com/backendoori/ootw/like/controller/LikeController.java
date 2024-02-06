package com.backendoori.ootw.like.controller;

import com.backendoori.ootw.like.dto.controller.LikeResponse;
import com.backendoori.ootw.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/v1/posts/{postId}/likes")
    public ResponseEntity<LikeResponse> pushLike(Authentication authentication,
                                                 @PathVariable Long postId) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(likeService.requestLike(userId, postId));
    }

}
