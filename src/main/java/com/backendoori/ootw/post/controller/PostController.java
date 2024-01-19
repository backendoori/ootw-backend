package com.backendoori.ootw.post.controller;

import java.net.URI;
import java.util.List;
import com.backendoori.ootw.post.dto.PostReadResponse;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.post.dto.PostSaveResponse;
import com.backendoori.ootw.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostSaveResponse> save(
        @RequestPart MultipartFile postImg,
        @RequestPart @Valid PostSaveRequest request) {
        PostSaveResponse response = postService.save(request, postImg);

        URI postUri = URI.create("/api/v1/posts/" + response.postId());

        return ResponseEntity.status(HttpStatus.CREATED)
            .location(postUri)
            .body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostReadResponse> readDetailByPostId(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(postService.getDetailByPostId(postId));
    }

    @GetMapping
    public ResponseEntity<List<PostReadResponse>> readAll() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(postService.getAll());
    }

}
