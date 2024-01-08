package com.backendoori.ootw.like.service;

import java.util.NoSuchElementException;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.like.domain.Like;
import com.backendoori.ootw.like.dto.controller.LikeResponse;
import com.backendoori.ootw.like.repository.LikeRepository;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.repository.PostRepository;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private static final String POST_NOT_FOUND_MESSAGE = "해당 게시글이 존재하지 않습니다.";
    private static final String LIKE_NOT_FOUND_MESSAGE = "찾으시는 좋아요 정보가 존재하지 않습니다.";

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public LikeResponse requestLike(Long userId, Long postId) {

        User user = userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND_MESSAGE));

        likeRepository.findByUserAndPost(user, post).ifPresentOrElse(
            Like::updateStatus,
            likeNotExist(user, post)
        );

        Like like = likeRepository.findByUserAndPost(user, post)
            .orElseThrow(() -> new NoSuchElementException(LIKE_NOT_FOUND_MESSAGE));

        return LikeResponse.from(like);
    }

    @NotNull
    private Runnable likeNotExist(User user, Post post) {
        return () -> {
            likeRepository.save(
                Like.builder()
                    .user(user)
                    .post(post)
                    .status(true)
                    .build());
        };
    }

}
