package com.backendoori.ootw.user.domain;

import com.backendoori.ootw.common.AssertUtil;
import com.backendoori.ootw.common.BaseEntity;
import com.backendoori.ootw.user.validation.Message;
import com.backendoori.ootw.user.validation.RFC5322;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Table(name = "users")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "certified", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean certified;

    public User(Long id, String email, String password, String nickname, String profileImageUrl, boolean certified) {
        AssertUtil.hasPattern(email, RFC5322.REGEX, Message.INVALID_EMAIL);
        AssertUtil.isTrue(email.length() <= 255, Message.TOO_LONG_EMAIL);
        AssertUtil.notBlank(password, Message.BLANK_PASSWORD);
        AssertUtil.notBlank(nickname, Message.BLANK_NICKNAME);
        AssertUtil.isTrue(nickname.length() <= 255, Message.TOO_LONG_NICKNAME);

        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.certified = certified;
    }

    public void certify() {
        this.certified = true;
    }

    public boolean matchPassword(PasswordEncoder passwordEncoder, String decrypted) {
        return passwordEncoder.matches(decrypted, password);
    }

}
