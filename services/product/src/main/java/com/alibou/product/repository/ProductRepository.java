package com.alibou.product.repository;

import com.alibou.product.entity.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByIdInOrderById(List<@NotNull(message = "Product is mandatory") Integer> productIds);
}
