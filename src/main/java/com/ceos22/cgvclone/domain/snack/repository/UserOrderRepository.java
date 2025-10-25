package com.ceos22.cgvclone.domain.snack.repository;

import com.ceos22.cgvclone.domain.snack.entity.UserOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserOrderRepository extends CrudRepository<UserOrder, Long> {
    Optional<UserOrder> findByUuid(UUID uuid);
}
