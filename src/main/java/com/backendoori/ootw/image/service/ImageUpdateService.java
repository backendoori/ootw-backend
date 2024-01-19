package com.backendoori.ootw.image.service;

import java.util.ArrayList;
import java.util.List;
import com.backendoori.ootw.avatar.domain.AvatarItem;
import com.backendoori.ootw.avatar.repository.AvatarItemRepository;
import com.backendoori.ootw.image.domain.Image;
import com.backendoori.ootw.image.repository.ImageRepository;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageUpdateService {

    private final PostRepository postRepository;
    private final AvatarItemRepository avatarItemRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    public static final String TIMEZONE = "Asia/Seoul";
    public static final String CRON = "0 20 00 10 * ?";

    @Scheduled(cron = CRON, zone = TIMEZONE) // 매월 20일 오전 00시 10분에 실행
    @Async
    @Transactional
    public void deleteUnused(){

        List<String> urls = new ArrayList<>();

        postRepository.findAll()
            .stream()
            .map(Post::getImageUrl)
            .forEach(urls::add);

        avatarItemRepository.findAll()
            .stream()
            .map(AvatarItem::getImageUrl)
            .forEach(urls::add);

        List<Image> images = imageRepository.findAll();

        for(Image image : images){
            if(!urls.contains(image.getImageUrl())){
                imageService.delete(image.getFileName());
            }
        }

    }

}
