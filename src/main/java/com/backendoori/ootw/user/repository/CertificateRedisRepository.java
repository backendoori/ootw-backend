package com.backendoori.ootw.user.repository;

import java.util.Optional;
import com.backendoori.ootw.user.domain.Certificate;
import org.springframework.data.repository.CrudRepository;

public interface CertificateRedisRepository extends CrudRepository<Certificate, String> {

    Optional<Certificate> findByEmail(String email);

}
