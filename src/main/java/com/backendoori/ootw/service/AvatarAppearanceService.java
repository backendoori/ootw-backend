package com.backendoori.ootw.service;

import com.backendoori.ootw.domain.AvatarItem;
import com.backendoori.ootw.dto.AvatarAppearanceRequest;
import com.backendoori.ootw.dto.AvatarAppearanceResponse;
import com.backendoori.ootw.repository.AvatarItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AvatarAppearanceService {

    private final ImageService imageService;
    private final AvatarItemRepository avatarItemRepository;

    public AvatarAppearanceResponse uploadItem(MultipartFile file, AvatarAppearanceRequest requestDto) {
        String url = imageService.uploadImage(file);
        AvatarItem savedItem = avatarItemRepository.save(AvatarItem.create(requestDto, url));
        return AvatarAppearanceResponse.from(savedItem);
    }

}
