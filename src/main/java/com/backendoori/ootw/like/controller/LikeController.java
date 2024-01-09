package com.backendoori.ootw.like.controller;

import com.backendoori.ootw.like.dto.controller.LikeRequest;
import com.backendoori.ootw.like.dto.controller.LikeResponse;
import com.backendoori.ootw.like.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("")
    public ResponseEntity<LikeResponse> pushLike(Authentication authentication,
                                                 @Valid @RequestBody LikeRequest requestDto) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(likeService.requestLike(userId, requestDto.postId()));
    }

}
