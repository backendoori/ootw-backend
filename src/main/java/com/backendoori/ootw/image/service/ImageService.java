package com.backendoori.ootw.image.service;

import com.backendoori.ootw.image.dto.ImageFile;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    ImageFile upload(MultipartFile file);

    void delete(String fileName);

}
