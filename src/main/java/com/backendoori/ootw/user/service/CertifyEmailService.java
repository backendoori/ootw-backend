package com.backendoori.ootw.user.service;

import com.backendoori.ootw.common.OotwMailSender;
import com.backendoori.ootw.user.dto.CertifyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertifyEmailService {

    private final OotwMailSender ootwMailSender;

    public void sendCertificate(String userEmail) {

    }

    public void certify(CertifyDto certifyDto) {

    }

}
