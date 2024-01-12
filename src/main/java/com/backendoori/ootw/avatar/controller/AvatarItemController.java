package com.backendoori.ootw.avatar.controller;

import java.util.List;
import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import com.backendoori.ootw.avatar.dto.AvatarItemResponse;
import com.backendoori.ootw.avatar.service.AvatarItemService;
import com.backendoori.ootw.common.validation.Image;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<AvatarItemResponse> upload(@RequestPart @Image MultipartFile file,
                                                     @RequestPart @Valid AvatarItemRequest request) {
        AvatarItemResponse avatarItem = appearanceService.upload(file, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(avatarItem);
    }

    @GetMapping
    public ResponseEntity<List<AvatarItemResponse>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(appearanceService.getList());
    }

}
