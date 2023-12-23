package com.backendoori.ootw.controller;

import com.backendoori.ootw.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class AvatarAppearanceController {

    private final ImageService imageService;

    // TODO: 추후 아바타 이미지를 업로드하고 저장하는 로직으로 변경
    @PostMapping("/api/v1/image")
    public ResponseEntity<String> uploadImage(@RequestPart("file")MultipartFile file){
        return ResponseEntity.ok(imageService.uploadImage(file));
    }

}
