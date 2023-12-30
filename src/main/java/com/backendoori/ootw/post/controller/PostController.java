package com.backendoori.ootw.post.controller;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import com.backendoori.ootw.exception.ExceptionResponse;
import com.backendoori.ootw.exception.ExceptionResponse.FieldErrorDetail;
import com.backendoori.ootw.post.dto.PostReadResponse;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.post.dto.PostSaveResponse;
import com.backendoori.ootw.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostSaveResponse> save(@RequestBody @Valid PostSaveRequest request) {
        PostSaveResponse response = postService.save(request);

        URI postUri = URI.create("/api/v1/posts/" + response.getPostId());

        return ResponseEntity.status(HttpStatus.CREATED)
            .location(postUri)
            .body(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostReadResponse> readDetailByPostId(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(postService.getDatailByPostId(postId));
    }

    @GetMapping
    public ResponseEntity<List<PostReadResponse>> readAll() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(postService.getAll());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse<String>> handleException(
        Exception e
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public <T extends Exception> ResponseEntity<ExceptionResponse<String>> handleRuntimeException(
        T e
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionResponse<String>> handleNoSuchElementException(
        NoSuchElementException e
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse<List<FieldErrorDetail>>> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse.from(e));
    }

}
