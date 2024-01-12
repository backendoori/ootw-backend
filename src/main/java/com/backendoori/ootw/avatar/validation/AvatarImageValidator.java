package com.backendoori.ootw.avatar.validation;

import com.backendoori.ootw.avatar.domain.ItemType;
import com.backendoori.ootw.avatar.domain.Sex;
import io.jsonwebtoken.lang.Assert;

public class AvatarImageValidator {

    private static final String NO_IMAGE_URL_MESSAGE = "아바타 이미지 url이 존재하지 않습니다.";
    private static final String ITEM_TYPE_ESSENTIAL = "아바타 타입은 반드시 포함되어야 합니다.";
    private static final String INVALID_ITEM_TYPE_MESSAGE = "해당 단어가 아바타 이미지 타입이 존재하지 않습니다.";
    private static final String SEX_ESSENTIAL = "성별은 반드시 포함되어야 합니다.";
    private static final String INVALID_WORD_MESSAGE = "해당 단어가 프로젝트 내 성별 분류 체계에 존재하지 않습니다.";

    public static void validateSex(String sex) {
        Assert.notNull(sex, SEX_ESSENTIAL);
        Assert.isTrue(!sex.isBlank(), SEX_ESSENTIAL);
        Assert.isTrue(Sex.checkValue(sex), INVALID_WORD_MESSAGE);
    }

    public static void validateItemType(String type) {
        Assert.notNull(type, ITEM_TYPE_ESSENTIAL);
        Assert.isTrue(!type.isBlank(), ITEM_TYPE_ESSENTIAL);
        Assert.isTrue(ItemType.checkValue(type), INVALID_ITEM_TYPE_MESSAGE);
    }

    public static void validateImage(String image) {
        Assert.notNull(image, NO_IMAGE_URL_MESSAGE);
        Assert.isTrue(!image.isBlank(), NO_IMAGE_URL_MESSAGE);
    }

}
