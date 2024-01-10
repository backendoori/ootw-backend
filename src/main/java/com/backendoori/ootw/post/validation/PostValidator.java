package com.backendoori.ootw.post.validation;

import static com.backendoori.ootw.post.validation.Message.BLANK_POST_CONTENT;
import static com.backendoori.ootw.post.validation.Message.BLANK_POST_TITLE;
import static com.backendoori.ootw.post.validation.Message.INVALID_POST_CONTENT;
import static com.backendoori.ootw.post.validation.Message.INVALID_POST_TITLE;
import static com.backendoori.ootw.post.validation.Message.NULL_POST;
import static com.backendoori.ootw.post.validation.Message.NULL_TEMPERATURE_ARRANGE;
import static com.backendoori.ootw.post.validation.Message.NULL_WRITER;

import com.backendoori.ootw.common.AssertUtil;
import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.weather.domain.TemperatureArrange;

public class PostValidator {

    private static final Integer MAX_TITLE_LENGTH = 30;
    private static final Integer MAX_CONTENT_LENGTH = 500;

    public static void validateUser(User user) {
        AssertUtil.notNull(user, NULL_WRITER);
    }

    public static void validatePostSaveRequest(PostSaveRequest request) {
        AssertUtil.notNull(request, NULL_POST);
        validateTitle(request.title());
        validateContent(request.content());
    }

    public static void validateTemperatureArrange(TemperatureArrange temperatureArrange) {
        AssertUtil.notNull(temperatureArrange, NULL_TEMPERATURE_ARRANGE);
    }

    private static void validateTitle(String title) {
        AssertUtil.notBlank(title, BLANK_POST_TITLE);
        AssertUtil.isTrue(title.length() > MAX_TITLE_LENGTH, INVALID_POST_TITLE);
    }

    private static void validateContent(String content) {
        AssertUtil.notBlank(content, BLANK_POST_CONTENT);
        AssertUtil.isTrue(content.length() > MAX_CONTENT_LENGTH, INVALID_POST_CONTENT);
    }

}
