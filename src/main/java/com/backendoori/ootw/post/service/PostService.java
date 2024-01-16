package com.backendoori.ootw.post.service;

import static com.backendoori.ootw.post.validation.Message.NULL_REQUEST;
import static com.backendoori.ootw.post.validation.Message.POST_NOT_FOUND;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import com.backendoori.ootw.common.image.ImageFile;
import com.backendoori.ootw.common.image.ImageService;
import com.backendoori.ootw.common.image.exception.SaveException;
import com.backendoori.ootw.exception.PermissionException;
import com.backendoori.ootw.exception.UserNotFoundException;
import com.backendoori.ootw.like.domain.Like;
import com.backendoori.ootw.like.repository.LikeRepository;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.dto.request.PostSaveRequest;
import com.backendoori.ootw.post.dto.request.PostUpdateRequest;
import com.backendoori.ootw.post.dto.response.PostReadResponse;
import com.backendoori.ootw.post.dto.response.PostSaveUpdateResponse;
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
        Assert.isTrue(Objects.nonNull(request), () -> {
            throw new IllegalArgumentException(NULL_REQUEST);
        });

        User user = userRepository.findById(getUserId())
            .orElseThrow(UserNotFoundException::new);
        TemperatureArrange temperatureArrange = weatherService.getCurrentTemperatureArrange(request.coordinate());

        if (Objects.isNull(postImg) || postImg.isEmpty()) {
            return savePostWithImageUrl(user, request, null, temperatureArrange);
        }

        ImageFile imgFile = imageService.upload(postImg);
        try {
            return savePostWithImageUrl(user, request, imgFile.url(), temperatureArrange);
        } catch (Exception e) {
            imageService.delete(imgFile.fileName());
            throw new SaveException();
        }
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

        Assert.notNull(request, () -> {
            throw new IllegalArgumentException(NULL_REQUEST);
        });

        post.updateTitle(request.title());
        post.updateContent(request.content());

        if (Objects.isNull(postImg) || postImg.isEmpty()) {
            return updatePostWithImageUrl(post, null);
        }

        ImageFile imgFile = imageService.upload(postImg);
        try {
            // TODO: 기존 저장된 이미지 삭제(원래 null인 경우도 있으니 주의)
            return updatePostWithImageUrl(post, imgFile.url());
        } catch (Exception e) {
            imageService.delete(imgFile.fileName());
            throw new SaveException();
        }
    }

    private PostSaveUpdateResponse updatePostWithImageUrl(Post post, String imgFile) {
        post.updateImageUrl(imgFile);

        return PostSaveUpdateResponse.from(post);
    }

    private PostSaveUpdateResponse savePostWithImageUrl(User user, PostSaveRequest request, String imgFile,
                                                        TemperatureArrange temperatureArrange) {
        Post savedPost = postRepository.save(Post.from(user, request, imgFile, temperatureArrange));

        return PostSaveUpdateResponse.from(savedPost);
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

    private boolean isLogin() {
        return !ANONYMOUS_USER_PRINCIPLE.equals(SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal());
    }

    private void checkUserHasPostPermission(Post post) {
        Assert.isTrue(post.getUser().isSameId(getUserId()), () -> {
            throw new PermissionException();
        });
    }

}
