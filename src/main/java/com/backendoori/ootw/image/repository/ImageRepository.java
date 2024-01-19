package com.backendoori.ootw.image.repository;

import com.backendoori.ootw.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
