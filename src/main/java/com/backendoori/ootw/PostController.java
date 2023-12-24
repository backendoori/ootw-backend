package com.backendoori.ootw;

import java.net.URI;
import com.backendoori.ootw.dto.PostSaveRequest;
import com.backendoori.ootw.dto.PostSaveResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

        return ResponseEntity.created(URI.create("/api/v1/posts/" + response.getPostId()))
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
    }

    // TODO: BindingResult 예외, 그 외 예외에 대한 응답 처리할 수 있도록 구체화
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse<Exception>> handleException(
        Exception e
    ) {
        return ResponseEntity.internalServerError()
            .body(ExceptionResponse.from(e));
    }

}
