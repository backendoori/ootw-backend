package com.backendoori.ootw.avatar.domain;

import static com.backendoori.ootw.avatar.validation.AvatarImageValidator.validateImage;
import static com.backendoori.ootw.avatar.validation.AvatarImageValidator.validateItemType;
import static com.backendoori.ootw.avatar.validation.AvatarImageValidator.validateSex;

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
    private ItemType itemType;

    @Column(name = "sex", nullable = false, columnDefinition = "varchar(10)")
    @Enumerated(EnumType.STRING)
    private Sex sex;

    private AvatarItem(String image, String type, String sex) {
        validateImage(image);
        validateItemType(type);
        validateSex(sex);

        this.image = image;
        this.itemType = ItemType.valueOf(type);
        this.sex = Sex.valueOf(sex);
    }

    public static AvatarItem create(AvatarItemRequest requestDto, String url) {
        return new AvatarItem(url, requestDto.type(), requestDto.sex());
    }

}
