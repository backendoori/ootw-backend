package com.backendoori.ootw.post.service;

import java.util.List;
import java.util.NoSuchElementException;
import com.backendoori.ootw.common.image.ImageService;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.PostReadResponse;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.post.dto.PostSaveResponse;
import com.backendoori.ootw.post.repository.PostRepository;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.UserRepository;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final WeatherService weatherService;

    @Transactional
    public PostSaveResponse save(PostSaveRequest request, MultipartFile postImg) {
        User user = userRepository.findById(getUserId())
            .orElseThrow(UserNotFoundException::new);
        String imgUrl = imageService.uploadImage(postImg);
        TemperatureArrange temperatureArrange = weatherService.getCurrentTemperatureArrange(request.location());

        Post savedPost = postRepository.save(Post.from(user, request, imgUrl, temperatureArrange));

        return PostSaveResponse.from(savedPost);
    }

    @Transactional(readOnly = true)
    public PostReadResponse getDetailByPostId(Long postId) {
        Post post = postRepository.findByIdWithUserEntityGraph(postId)
            .orElseThrow(() -> new NoSuchElementException("해당하는 게시글이 없습니다."));

        return PostReadResponse.from(post);
    }

    @Transactional(readOnly = true)
    public List<PostReadResponse> getAll() {
        return postRepository.findAllWithUserEntityGraph()
            .stream()
            .map(PostReadResponse::from)
            .toList();
    }

    private long getUserId() {
        return (long) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
    }

}
