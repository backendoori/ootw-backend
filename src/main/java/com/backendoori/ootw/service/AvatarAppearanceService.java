package com.backendoori.ootw.service;

import com.backendoori.ootw.domain.AvatarItem;
import com.backendoori.ootw.dto.AvatarAppearanceRequestDto;
import com.backendoori.ootw.repository.AvatarItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AvatarAppearanceService {

    private final ImageService imageService;
    private final AvatarItemRepository avatarItemRepository;
    public AvatarItem uploadItem(MultipartFile file, AvatarAppearanceRequestDto requestDto) {
        String url = imageService.uploadImage(file);
        return avatarItemRepository.save(AvatarItem.create(requestDto, url));
    }

}
