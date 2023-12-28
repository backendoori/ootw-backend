package com.backendoori.ootw.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.backendoori.ootw.dto.AvatarAppearanceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AvatarItemTest {

    @Test
    @DisplayName("아바타 옷 생성 테스트")
    public void createTest() throws Exception {
        //given
        AvatarAppearanceRequest request = new AvatarAppearanceRequest("HAIR", true);
        String url = "url";

        //when
        AvatarItem avatarItem = AvatarItem.create(request, url);


        //then
        assertThat(request.type()).isEqualTo(avatarItem.getType().name());
        assertThat(request.sex()).isEqualTo(avatarItem.isSex());
    }
}
