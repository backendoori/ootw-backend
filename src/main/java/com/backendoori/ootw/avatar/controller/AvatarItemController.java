package com.backendoori.ootw.avatar.controller;

import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import com.backendoori.ootw.avatar.dto.AvatarItemResponse;
import com.backendoori.ootw.avatar.service.AvatarItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
public class AvatarItemController {

    private final AvatarItemService appearanceService;

    @PostMapping
    public ResponseEntity<AvatarItemResponse> uploadImage(@RequestPart("file") MultipartFile file,
                                                          @RequestBody AvatarItemRequest requestDto) {
        AvatarItemResponse avatarItem = appearanceService.uploadItem(file, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(avatarItem);
    }

}
