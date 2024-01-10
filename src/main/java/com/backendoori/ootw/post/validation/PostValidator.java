package com.backendoori.ootw.post.validation;

import static com.backendoori.ootw.post.validation.Message.BLANK_POST_CONTENT;
import static com.backendoori.ootw.post.validation.Message.BLANK_POST_TITLE;
import static com.backendoori.ootw.post.validation.Message.INVALID_POST_CONTENT;
import static com.backendoori.ootw.post.validation.Message.INVALID_POST_TITLE;
import static com.backendoori.ootw.post.validation.Message.NULL_POST;
import static com.backendoori.ootw.post.validation.Message.NULL_TEMPERATURE_ARRANGE;
import static com.backendoori.ootw.post.validation.Message.NULL_WRITER;

import com.backendoori.ootw.post.dto.PostSaveRequest;
import com.backendoori.ootw.user.domain.User;
import com.backendoori.ootw.weather.domain.TemperatureArrange;
import org.springframework.util.Assert;

public class PostValidator {

    private static final Integer MAX_TITLE_LENGTH = 30;
    private static final Integer MAX_CONTENT_LENGTH = 500;

    public static void validateUser(User user) {
        Assert.notNull(user, NULL_WRITER);
    }

    public static void validatePostSaveRequest(PostSaveRequest request) {
        Assert.notNull(request, NULL_POST);
        validateTitle(request.title());
        validateContent(request.content());
    }

    public static void validateTemperatureArrange(TemperatureArrange temperatureArrange) {
        Assert.notNull(temperatureArrange, NULL_TEMPERATURE_ARRANGE);
    }

    private static void validateTitle(String title) {
        Assert.notNull(title, BLANK_POST_TITLE);
        Assert.isTrue(!title.isBlank(), BLANK_POST_TITLE);
        Assert.isTrue(!(title.length() > MAX_TITLE_LENGTH), INVALID_POST_TITLE);
    }

    private static void validateContent(String content) {
        Assert.notNull(content, BLANK_POST_CONTENT);
        Assert.isTrue(!content.isBlank(), BLANK_POST_CONTENT);
        Assert.isTrue(!(content.length() > MAX_CONTENT_LENGTH), INVALID_POST_CONTENT);
    }

}
