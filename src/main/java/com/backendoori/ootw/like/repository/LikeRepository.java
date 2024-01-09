package com.backendoori.ootw.like.repository;

import java.util.Optional;
import com.backendoori.ootw.like.domain.Like;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndPost(User user, Post post);

}
