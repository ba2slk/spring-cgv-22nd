package com.ceos22.cgvclone.domain.snack.repository;

import com.ceos22.cgvclone.domain.snack.entity.UserOrder;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<UserOrder, Long> {
}
