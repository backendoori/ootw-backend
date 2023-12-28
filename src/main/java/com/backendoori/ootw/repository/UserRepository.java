package com.backendoori.ootw.repository;

import com.backendoori.ootw.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
