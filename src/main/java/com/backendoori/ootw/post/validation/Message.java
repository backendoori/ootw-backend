package com.backendoori.ootw.post.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Message {

    public static final String POST_NOT_FOUND = "해당하는 게시글이 없습니다.";
    public static final String NULL_POST = "게시글 생성/수정 요청 정보가 null이어서는 안됩니다.";

    public static final String NULL_WRITER = "게시글 생성 요청 사용자가 null이어서는 안됩니다.";

    public static final String NULL_TEMPERATURE_ARRANGE = "기상청 API에서 기온 범위 값을 null로 반환했습니다.";

    public static final String BLANK_POST_TITLE = "게시글 제목이 null이거나 공백이어서는 안됩니다.";
    public static final String INVALID_POST_TITLE = "게시글 제목은 30자 이내여야 합니다.";

    public static final String BLANK_POST_CONTENT = "게시글 내용이 null이거나 공백이어서는 안됩니다.";
    public static final String INVALID_POST_CONTENT = "게시글 내용은 500자 이내여야 합니다.";

}
