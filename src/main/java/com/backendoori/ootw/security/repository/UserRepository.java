package com.backendoori.ootw.security.repository;

import java.util.Optional;
import com.backendoori.ootw.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
