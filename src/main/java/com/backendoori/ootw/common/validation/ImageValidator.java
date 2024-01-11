package com.backendoori.ootw.common.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageValidator {

    private static final String EMPTY_FILE = "비어있는 파일은 업로드 할 수 없습니다.";
    private static final String FILE_OVER_SIZE = "파일이 허용된 최대 크기를 초과했습니다.";
    private static final String INVALID_FILE_TYPE = "지원하지 않는 형식의 파일입니다.";

    public static void validateImage(MultipartFile img) {
        if (img == null) {
            throw new IllegalArgumentException(EMPTY_FILE);
        }
        if (img.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_FILE);
        }

        if (img.getSize() > 10_000_000) {
            throw new IllegalArgumentException(FILE_OVER_SIZE);
        }

        String contentType = img.getContentType();
        if (!contentType.startsWith("image")) {
            throw new IllegalArgumentException(INVALID_FILE_TYPE);
        }
    }

}
