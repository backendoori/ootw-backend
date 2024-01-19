package com.backendoori.ootw.image.dto;

import com.backendoori.ootw.image.domain.Image;

public record ImageFile(
    String url,
    String fileName
) {
    public static ImageFile from(Image image){
        return new ImageFile(image.getImageUrl(), image.getFileName());
    }
}
