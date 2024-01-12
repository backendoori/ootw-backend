package com.backendoori.ootw.post.service;

import static com.backendoori.ootw.post.validation.Message.POST_NOT_FOUND;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import com.backendoori.ootw.common.image.ImageService;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.like.domain.Like;
import com.backendoori.ootw.like.repository.LikeRepository;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.request.PostSaveRequest;
import com.backendoori.ootw.post.dto.request.PostUpdateRequest;
import com.backendoori.ootw.post.dto.response.PostReadResponse;
import com.backendoori.ootw.post.dto.response.PostSaveUpdateResponse;
import com.backendoori.ootw.post.exception.NoPostPermissionException;
import com.backendoori.ootw.post.exception.ResourceNotExistException;
import com.backendoori.ootw.post.repository.PostRepository;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.user.repository.UserRepository;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import com.backendoori.ootw.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
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
    public PostSaveUpdateResponse save(PostSaveRequest request, MultipartFile postImg) {
        User user = userRepository.findById(getUserId())
            .orElseThrow(UserNotFoundException::new);

        // TODO: 이미지가 null인 경우 설정하기
        String imgUrl = imageService.uploadImage(postImg);
        TemperatureArrange temperatureArrange = weatherService.getCurrentTemperatureArrange(request.coordinate());

        Post savedPost = postRepository.save(Post.from(user, request, imgUrl, temperatureArrange));

        return PostSaveUpdateResponse.from(savedPost);
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

    @Transactional
    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND));

        checkUserHasPostPermission(post);

        postRepository.delete(post);
    }


    @Transactional
    public PostSaveUpdateResponse update(Long postId, MultipartFile postImg, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new NoSuchElementException(POST_NOT_FOUND));

        checkUserHasPostPermission(post);

        Assert.isTrue(Objects.nonNull(request) || Objects.nonNull(postImg), () -> {
            throw new ResourceNotExistException();
        });

        if (Objects.nonNull(request)) {
            post.setTitleAndContent(request);
        }

        if (Objects.nonNull(postImg)) {
            // TODO: 기존 저장된 이미지 삭제(원래 null인 경우도 있으니 주의)
            //  imageService.uploadImage(postImg)가 잘못 저장되어 null 인 경우도 있을까..?
            post.setImage(imageService.uploadImage(postImg));
        }

        return PostSaveUpdateResponse.from(post);
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
    // 세희) 위에서처럼 정의하면 constant pool에 저장이되고 중복되는 값이 있으면 비슷한 걸로 인식한다고는 들었습니다!
    // 다만 실수를 줄이고 더 안전하게 하고 싶다면 equals를 사용하는 것이 나을 수도 있겟네여!
    private boolean isLogin() {
        return SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal() != ANONYMOUS_USER_PRINCIPLE;
    }

    private void checkUserHasPostPermission(Post post) {
        Assert.isTrue(getUserId() == post.getUser().getId(), () -> {
            throw new NoPostPermissionException();
        });
    }

}
