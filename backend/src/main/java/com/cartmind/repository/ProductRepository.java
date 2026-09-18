package com.cartmind.repository;

import com.cartmind.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIgnoreCase(String category);

    List<Product> findByBrandIgnoreCase(String brand);

    List<Product> findByPriceLessThanEqual(BigDecimal price);

    List<Product> findByCategoryIgnoreCaseAndPriceLessThanEqual(
            String category,
            BigDecimal price
    );

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByProviderIgnoreCase(String provider);
}
