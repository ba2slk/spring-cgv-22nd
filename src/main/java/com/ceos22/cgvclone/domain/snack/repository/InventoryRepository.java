package com.ceos22.cgvclone.domain.snack.repository;

import com.ceos22.cgvclone.domain.snack.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
}
