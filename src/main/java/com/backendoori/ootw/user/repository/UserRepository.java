package com.backendoori.ootw.user.repository;

import java.util.Optional;
import com.backendoori.ootw.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
