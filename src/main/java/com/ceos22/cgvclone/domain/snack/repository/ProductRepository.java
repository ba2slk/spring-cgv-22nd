package com.ceos22.cgvclone.domain.snack.repository;

import com.ceos22.cgvclone.domain.snack.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
