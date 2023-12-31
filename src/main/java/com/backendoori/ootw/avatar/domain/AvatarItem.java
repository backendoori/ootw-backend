package com.backendoori.ootw.avatar.domain;

import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "avatar_items")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvatarItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "type", nullable = false, columnDefinition = "varchar(30)")
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "sex", nullable = false, columnDefinition = "tinyint")
    private boolean sex;

    private AvatarItem(String image, String type, boolean sex) {
        this.image = image;
        this.type = Type.valueOf(type);
        this.sex = sex;
    }

    public static AvatarItem create(AvatarItemRequest requestDto, String url) {
        return new AvatarItem(url, requestDto.type(), requestDto.sex());
    }

}
