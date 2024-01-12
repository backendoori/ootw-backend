package com.backendoori.ootw.common.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    ImageFile upload(MultipartFile file);

    void delete(String fileName);

}
