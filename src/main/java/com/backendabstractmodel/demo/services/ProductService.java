package com.backendabstractmodel.demo.services;

import com.backendabstractmodel.demo.domain.dto.ProductDTO;
import com.backendabstractmodel.demo.domain.dto.pagination.ProductPaginationDTO;
import com.backendabstractmodel.demo.domain.dto.search_result.PageResult;
import com.backendabstractmodel.demo.domain.entity.Product;
import com.backendabstractmodel.demo.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService extends AbstractCrudService<UUID, Product, ProductDTO> {

    private final ProductRepository repository;

    @Override
    protected void postConstructorInit() {
        initRefs(repository);
    }

    public PageResult<ProductDTO> getPagedList(ProductPaginationDTO dto) {
        return toPageResult( repository.findByPage(dto) );
    }

}
