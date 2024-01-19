package com.backendoori.ootw.avatar.repository;

import com.backendoori.ootw.avatar.domain.AvatarItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarItemRepository extends JpaRepository<AvatarItem, Long> {

}
