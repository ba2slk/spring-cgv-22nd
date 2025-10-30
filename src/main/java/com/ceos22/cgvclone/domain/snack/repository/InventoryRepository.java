package com.ceos22.cgvclone.domain.snack.repository;

import com.ceos22.cgvclone.domain.snack.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @EntityGraph(attributePaths = "item")
    List<Inventory> findAllByTheaterId(Long theaterId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i JOIN FETCH i.item " +
            "WHERE i.theater.id = :theaterId AND i.item.id IN :itemIds")
    List<Inventory> findByTheaterIdAndItemIdInWithLock(
            @Param("theaterId") Long theaterId,
            @Param("itemIds") List<Long> itemIds
    );

}
