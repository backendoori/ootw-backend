package com.backendoori.ootw.post.validation;

import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import org.springframework.util.Assert;

public class PostValidator {

    private static final Integer MAX_TITLE_LENGTH = 30;
    private static final Integer MAX_CONTENT_LENGTH = 500;

    public static void validateUser(User user) {
        Assert.notNull(user, "게시글 생성 요청 사용자가 null이어서는 안됩니다.");
    }

    public static void validatePostSaveRequest(PostSaveRequest request) {
        Assert.notNull(request, "게시글 생성 요청 정보가 null이어서는 안됩니다.");
        validateTitle(request.title());
        validateContent(request.content());
    }

    public static void validateTemperatureArrange(TemperatureArrange temperatureArrange) {
        Assert.notNull(temperatureArrange, "게시글 기온 범위가 null이어서는 안됩니다.");
    }

    private static void validateTitle(String title) {
        Assert.notNull(title, "게시글 제목이 null이어서는 안됩니다.");
        Assert.isTrue(!title.isBlank(), "게시글 제목이 공백이어서는 안됩니다.");
        Assert.isTrue(!(title.length() > MAX_TITLE_LENGTH), "게시글 제목은 30자 이내여야 합니다.");
    }

    private static void validateContent(String content) {
        Assert.notNull(content, "게시글 내용이 null이어서는 안됩니다.");
        Assert.isTrue(!content.isBlank(), "게시글 내용이 공백이어서는 안됩니다.");
        Assert.isTrue(!(content.length() > MAX_CONTENT_LENGTH), "게시글 내용은 500자 이내여야 합니다.");
    }

}
