package com.backendoori.ootw.post.service;

import java.util.List;
import java.util.NoSuchElementException;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.PostReadResponse;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.post.dto.PostSaveResponse;
import com.backendoori.ootw.post.repository.PostRepository;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostSaveResponse save(PostSaveRequest request) {
        // TODO: 사용자 인증/인가 로직 추가
        User user = userRepository.findById(request.userId())
            .orElseThrow(() ->
                new NoSuchElementException("해당하는 유저가 없습니다.")
            );

        Post savedPost = postRepository.save(Post.from(user, request));

        return PostSaveResponse.from(savedPost);
    }

    @Transactional(readOnly = true)
    public PostReadResponse getDatailByPostId(Long postId) {
        Post post = postRepository.findByIdWithUser(postId)
            .orElseThrow(() ->
                new NoSuchElementException("해당하는 게시글이 없습니다.")
            );

        return PostReadResponse.from(post);
    }

    @Transactional(readOnly = true)
    public List<PostReadResponse> getAll() {
        return postRepository.findAllWithUser()
            .stream()
            .map(PostReadResponse::from)
            .toList();
    }
}
