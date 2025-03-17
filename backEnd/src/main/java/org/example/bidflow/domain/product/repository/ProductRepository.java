package org.example.bidflow.domain.product.repository;

import org.example.bidflow.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
