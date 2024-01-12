package com.backendoori.ootw.avatar.service;

import java.util.List;
import com.backendoori.ootw.avatar.domain.AvatarItem;
import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import com.backendoori.ootw.avatar.dto.AvatarItemResponse;
import com.backendoori.ootw.avatar.repository.AvatarItemRepository;
import com.backendoori.ootw.common.image.ImageFile;
import com.backendoori.ootw.common.image.ImageService;
import com.backendoori.ootw.common.image.exception.SaveException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AvatarItemService {

    private final ImageService imageService;
    private final AvatarItemRepository avatarItemRepository;

    @Transactional
    public AvatarItemResponse upload(MultipartFile file, AvatarItemRequest requestDto) {
        ImageFile imageFile = imageService.upload(file);
        try {
            String url = imageFile.url();
            AvatarItem savedItem = avatarItemRepository.save(AvatarItem.create(requestDto, url));

            return AvatarItemResponse.from(savedItem);
        } catch (Exception e) {
            imageService.delete(imageFile.fileName());
            throw new SaveException();
        }
    }

    public List<AvatarItemResponse> getList() {
        return avatarItemRepository.findAll()
            .stream()
            .map(AvatarItemResponse::from)
            .toList();
    }

}
