package com.backendoori.ootw.common.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    ImageFile uploadImage(MultipartFile file);

    void deleteImage(String fileName);

}
