package com.backendoori.ootw.repository;

import com.backendoori.ootw.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
