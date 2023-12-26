package com.backendoori.ootw.dto;

import com.backendoori.ootw.domain.User;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostWriterInfo {

    private final Long userId;
    private final String nickname;
    private final String image;

    public static PostWriterInfo from(User user) {
        return new PostWriterInfo(user.getId(), user.getNickname(), user.getImage());
    }

}
