package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByCategoryCategoryName(String categoryCategoryName);
}
