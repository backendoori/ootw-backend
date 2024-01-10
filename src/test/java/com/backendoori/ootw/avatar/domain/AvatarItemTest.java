package com.backendoori.ootw.avatar.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.backendoori.ootw.avatar.dto.AvatarItemRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AvatarItemTest {

    @Test
    @DisplayName("아바타 옷 생성 테스트")
    public void createTest() throws Exception {
        //given
        AvatarItemRequest request = new AvatarItemRequest("HAIR", Sex.MALE.name());
        String url = "url";

        //when
        AvatarItem avatarItem = AvatarItem.create(request, url);


        //then
        assertThat(request.type()).isEqualTo(avatarItem.getItemType().name());
        assertThat(request.sex()).isEqualTo(avatarItem.getSex().name());
    }

}
