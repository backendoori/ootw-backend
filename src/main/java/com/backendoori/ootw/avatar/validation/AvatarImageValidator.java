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
        if(sex == null){
            throw new IllegalArgumentException(SEX_ESSENTIAL);
        }
        if (sex.isBlank()) {
            throw new IllegalArgumentException(SEX_ESSENTIAL);
        }
        if (!Sex.checkValue(sex)) {
            throw new IllegalArgumentException(INVALID_WORD_MESSAGE);
        }
    }

    public static void validateItemType(String type) {
        if(type == null){
            throw new IllegalArgumentException(SEX_ESSENTIAL);
        }
        if (type.isBlank()) {
            throw new IllegalArgumentException(ITEM_TYPE_ESSENTIAL);
        }
        if (!ItemType.checkValue(type)) {
            throw new IllegalArgumentException(INVALID_ITEM_TYPE_MESSAGE);
        }
    }

    public static void validateImage(String image) {
        if(image == null){
            throw new IllegalArgumentException(SEX_ESSENTIAL);
        }
        if (image.isBlank()) {
            throw new IllegalArgumentException(NO_IMAGE_URL_MESSAGE);
        }
    }

}
