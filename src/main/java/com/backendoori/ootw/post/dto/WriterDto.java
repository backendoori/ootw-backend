package com.backendoori.ootw.post.dto;

import com.backendoori.ootw.user.domain.User;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WriterDto {

    private final Long userId;
    private final String nickname;
    private final String image;

    public static WriterDto from(User user) {
        return new WriterDto(user.getId(), user.getNickname(), user.getImage());
    }

}
