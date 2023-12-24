package com.backendoori.ootw.repository;

import java.util.Optional;
import com.backendoori.ootw.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "user")
    @Query("select p from Post p where p.id = :postId")
    Optional<Post> findByIdWithEntityGraph(@Param("postId") Long postId);

}
