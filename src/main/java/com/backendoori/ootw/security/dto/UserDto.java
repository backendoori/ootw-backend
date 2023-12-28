package com.backendoori.ootw.security.dto;

import java.time.LocalDateTime;
import com.backendoori.ootw.domain.User;
import lombok.Builder;

@Builder
public record UserDto(
    Long id,
    String email,
    String nickname,
    String image,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

) {

    public static UserDto from(User user) {
        return UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .image(user.getImage())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

}
