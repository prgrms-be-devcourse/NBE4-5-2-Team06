package org.example.bidflow.domain.product.repository;

import org.example.bidflow.domain.product.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductName(String productName);
}
