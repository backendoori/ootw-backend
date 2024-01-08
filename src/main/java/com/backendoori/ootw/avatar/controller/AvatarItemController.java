package com.backendoori.ootw.avatar.controller;

import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import com.backendoori.ootw.avatar.dto.AvatarItemResponse;
import com.backendoori.ootw.avatar.service.AvatarItemService;
import com.backendoori.ootw.common.validation.Image;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/avatar-items")
@RequiredArgsConstructor
public class AvatarItemController {

    private final AvatarItemService appearanceService;

    @PostMapping
    public ResponseEntity<AvatarItemResponse> uploadImage(@RequestPart @Image MultipartFile file,
                                                          @RequestPart @Valid AvatarItemRequest request) {
        AvatarItemResponse avatarItem = appearanceService.uploadItem(file, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(avatarItem);
    }

}
