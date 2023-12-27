package com.backendoori.ootw.controller;

import com.backendoori.ootw.domain.AvatarItem;
import com.backendoori.ootw.dto.AvatarAppearanceRequest;
import com.backendoori.ootw.dto.AvatarAppearanceResponse;
import com.backendoori.ootw.service.AvatarAppearanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class AvatarAppearanceController {

    private final AvatarAppearanceService appearanceService;

    @PostMapping("/api/v1/image")
    public ResponseEntity<AvatarAppearanceResponse> uploadImage(@RequestPart("file")MultipartFile file,
                                                                @RequestBody AvatarAppearanceRequest requestDto){
        AvatarAppearanceResponse avatarItem = appearanceService.uploadItem(file, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(avatarItem);
    }

}
