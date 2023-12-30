package com.backendoori.ootw.avtar.service;

import com.backendoori.ootw.avtar.domain.AvatarItem;
import com.backendoori.ootw.avtar.dto.AvatarItemRequest;
import com.backendoori.ootw.avtar.dto.AvatarItemResponse;
import com.backendoori.ootw.avtar.repository.AvatarItemRepository;
import com.backendoori.ootw.common.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AvatarItemService {

    private final ImageService imageService;
    private final AvatarItemRepository avatarItemRepository;

    public AvatarItemResponse uploadItem(MultipartFile file, AvatarItemRequest requestDto) {
        String url = imageService.uploadImage(file);
        AvatarItem savedItem = avatarItemRepository.save(AvatarItem.create(requestDto, url));

        return AvatarItemResponse.from(savedItem);
    }

}
