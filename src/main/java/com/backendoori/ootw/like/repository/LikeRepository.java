package com.backendoori.ootw.like.repository;

import java.util.List;
import java.util.Optional;
import com.backendoori.ootw.like.domain.Like;
import com.backendoori.ootw.post.domain.Post;
import com.backendoori.ootw.user.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Like> findByUserAndPost(User user, Post post);

    @Query("select l from Like l where l.user.id = :userId and l.post = :post")
    Optional<Like> findByUserIdAndPost(@Param("userId") Long userId, @Param("post") Post post);

    @Query("select l from Like l where l.user.id = :userId and l.isLike = :isLike")
    List<Like> findByUserAndIsLike(@Param("userId") Long userId, @Param("isLike") boolean isLike);

}
