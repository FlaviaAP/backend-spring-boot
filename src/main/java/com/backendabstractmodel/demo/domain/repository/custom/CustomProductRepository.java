package com.backendabstractmodel.demo.domain.repository.custom;

import com.backendabstractmodel.demo.domain.dto.pagination.ProductPaginationDTO;
import com.backendabstractmodel.demo.domain.entity.Product;
import org.springframework.data.domain.Page;

public interface CustomProductRepository {

    Page<Product> findByPage(ProductPaginationDTO dto);

}
