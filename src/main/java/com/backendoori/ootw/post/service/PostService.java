package com.backendoori.ootw.post.service;

import static com.backendoori.ootw.post.validation.Message.POST_NOT_FOUND;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import com.backendoori.ootw.common.image.ImageService;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.like.domain.Like;
import com.backendoori.ootw.like.repository.LikeRepository;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.request.PostSaveRequest;
import com.backendoori.ootw.post.dto.request.PostUpdateRequest;
import com.backendoori.ootw.post.dto.response.PostReadResponse;
import com.backendoori.ootw.post.dto.response.PostSaveResponse;
import com.backendoori.ootw.post.exception.NoPostPermissionException;
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

    private static final String ANONYMOUS_USER_PRINCIPLE = "anonymousUser";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final WeatherService weatherService;
    private final LikeRepository likeRepository;

    @Transactional
    public PostSaveResponse save(PostSaveRequest request, MultipartFile postImg) {
        User user = userRepository.findById(getUserId())
            .orElseThrow(UserNotFoundException::new);
        String imgUrl = imageService.uploadImage(postImg);
        TemperatureArrange temperatureArrange = weatherService.getCurrentTemperatureArrange(request.coordinate());

        Post savedPost = postRepository.save(Post.from(user, request, imgUrl, temperatureArrange));

        return PostSaveResponse.from(savedPost);
    }

    @Transactional(readOnly = true)
    public PostReadResponse getDetailByPostId(Long postId) {
        Post post = postRepository.findByIdWithUserEntityGraph(postId)
            .orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND));
        PostReadResponse response = PostReadResponse.from(post);

        if (!isLogin()) {
            return response;
        }

        Optional<Like> like = likeRepository.findByUserIdAndPost(getUserId(), post);
        like.ifPresent(
            existLike -> {
                if (existLike.getIsLike()) {
                    response.updateIsLike();
                }
            }
        );

        return response;
    }

    @Transactional(readOnly = true)
    public List<PostReadResponse> getAll() {
        List<PostReadResponse> postResponseList = postRepository.findAllWithUserEntityGraph()
            .stream()
            .map(PostReadResponse::from)
            .toList();
        if (!isLogin()) {
            return postResponseList;
        }

        List<Long> likes = getLikedPostId(getUserId());
        if (likes.isEmpty()) {
            return postResponseList;
        }

        return postResponseList.stream()
            .peek(post -> {
                if (likes.contains(post.getPostId())) {
                    post.updateIsLike();
                }
            }).toList();
    }

    private List<Long> getLikedPostId(long userId) {
        return likeRepository.findByUserAndIsLike(userId, true)
            .stream().map(like -> like.getPost().getId())
            .toList();
    }

    private long getUserId() {
        return (long) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
    }

    //TODO: 이 부분을 .equals 써야하는지 궁금하다.
    private boolean isLogin() {
        return SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal() != ANONYMOUS_USER_PRINCIPLE;
    }

}
