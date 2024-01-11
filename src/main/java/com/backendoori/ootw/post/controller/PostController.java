package com.backendoori.ootw.post.controller;

import java.net.URI;
import java.util.List;
import com.backendoori.ootw.common.validation.Image;
import com.backendoori.ootw.post.dto.request.PostSaveRequest;
import com.backendoori.ootw.post.dto.request.PostUpdateRequest;
import com.backendoori.ootw.post.dto.response.PostReadResponse;
import com.backendoori.ootw.post.dto.response.PostSaveUpdateResponse;
import com.backendoori.ootw.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostService postService;

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

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        postService.delete(postId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .build();
    }

    @PostMapping
    public ResponseEntity<PostSaveUpdateResponse> save(
        @RequestPart(required = false) @Image(ignoreCase = true) MultipartFile postImg,
        @RequestPart @Valid PostSaveRequest request) {
        PostSaveUpdateResponse response = postService.save(request, postImg);

        return ResponseEntity.status(HttpStatus.CREATED)
            .location(getPostUri(response.postId()))
            .body(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostSaveUpdateResponse> update(
        @PathVariable Long postId,
        @RequestPart(required = false) @Image(ignoreCase = true) MultipartFile postImg,
        @RequestPart(required = false) @Valid PostUpdateRequest request) {
        PostSaveUpdateResponse response = postService.update(postId, postImg, request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .location(getPostUri(response.postId()))
            .body(response);
    }

    private URI getPostUri(Long postId) {
        return URI.create("/api/v1/posts/" + postId);
    }

}
