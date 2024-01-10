package com.backendoori.ootw.user.repository;

import com.backendoori.ootw.user.domain.Certificate;
import org.springframework.data.repository.CrudRepository;

public interface CertificateRedisRepository extends CrudRepository<Certificate, String> {

}
