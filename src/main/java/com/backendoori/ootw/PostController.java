package com.backendoori.ootw;

import java.net.URI;
import com.backendoori.ootw.dto.PostSaveRequest;
import com.backendoori.ootw.dto.PostSaveResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

}
