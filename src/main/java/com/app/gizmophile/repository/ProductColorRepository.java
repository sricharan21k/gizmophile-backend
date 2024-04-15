package com.app.gizmophile.repository;

import com.app.gizmophile.model.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {
}
