package com.backendoori.ootw.post.dto.response;

import com.backendoori.ootw.user.domain.User;

public record WriterDto(
    Long userId,
    String nickname,
    String image
) {

    public static WriterDto from(User user) {
        return new WriterDto(user.getId(), user.getNickname(), user.getImage());
    }

}
