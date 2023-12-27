package com.backendoori.ootw.controller;

import com.backendoori.ootw.domain.AvatarItem;
import com.backendoori.ootw.dto.AvatarAppearanceRequestDto;
import com.backendoori.ootw.service.AvatarAppearanceService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Void> uploadImage(@RequestPart("file")MultipartFile file,
                                              @RequestBody AvatarAppearanceRequestDto requestDto){
        AvatarItem avatarItem = appearanceService.uploadItem(file, requestDto);
        return ResponseEntity.created(URI.create("/avatar-image/" + avatarItem)).build();
    }

}
