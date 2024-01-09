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

    @Column(name = "image")
    private String image;

    @Column(name = "certified", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean certified;

    public User(Long id, String email, String password, String nickname, String image, Boolean certified) {
        AssertUtil.hasPattern(email, RFC5322.REGEX, Message.INVALID_EMAIL);
        AssertUtil.notBlank(password, Message.BLANK_PASSWORD);
        AssertUtil.notBlank(nickname, Message.BLANK_NICKNAME);

        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.image = image;
        this.certified = certified;
    }

}
