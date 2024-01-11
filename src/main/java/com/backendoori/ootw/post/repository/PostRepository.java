package com.backendoori.ootw.post.repository;

import java.util.List;
import java.util.Optional;
import com.backendoori.ootw.post.domain.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "user")
    @Query("select p from Post p where p.id = :postId")
    Optional<Post> findByIdWithUserEntityGraph(@Param("postId") Long postId);

    @EntityGraph(attributePaths = "user")
    @Query("select p from Post p order by p.createdAt desc")
    List<Post> findAllWithUserEntityGraph();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Post p where p.id = :postId")
    Optional<Post> findByIdForUpdateLikeCount(@Param("postId") Long postId);

}
