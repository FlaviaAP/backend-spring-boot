package com.backendabstractmodel.demo.domain.repository;

import com.backendabstractmodel.demo.domain.entity.Product;
import com.backendabstractmodel.demo.domain.repository.custom.CustomProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends CustomProductRepository, JpaRepository<Product, UUID> {
}
