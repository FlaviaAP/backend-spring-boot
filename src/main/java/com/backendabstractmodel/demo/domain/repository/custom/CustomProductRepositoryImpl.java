package com.backendabstractmodel.demo.domain.repository.custom;

import com.backendabstractmodel.demo.domain.dto.pagination.ProductPaginationDTO;
import com.backendabstractmodel.demo.domain.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import javax.persistence.EntityManager;
import java.text.MessageFormat;
import java.util.*;

public class CustomProductRepositoryImpl extends AbstractCustomRepository<Product> implements CustomProductRepository {

    private static final String ENTITY_ALIAS = "s";

    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Product> findByPage(ProductPaginationDTO dto) {
        StringBuilder queryBuilder = new StringBuilder(
            MessageFormat.format("SELECT new Country({0}.idIso, {0}.name) FROM Country {0} ", ENTITY_ALIAS)
        );
        StringBuilder countQueryBuilder = new StringBuilder(
            String.format("SELECT COUNT(0) FROM Country %s ", ENTITY_ALIAS)
        );

        List<String> conditions = new ArrayList<>();
        Map<String, Object> parametersMap = buildParametersMap(dto, conditions);

        return findByPage(
            ENTITY_ALIAS,
            dto.toPageable(), entityManager,
            queryBuilder, countQueryBuilder,
            parametersMap, conditions
        );
    }

    private Map<String, Object> buildParametersMap(ProductPaginationDTO dto, List<String> conditions) {
        Map<String, Object> parametersMap = new HashMap<>();

        if(Objects.nonNull(dto.getName())) {
            parametersMap.put("name", "%" + dto.getName().toUpperCase() + "%");
            conditions.add(String.format("UPPER(%s.name) LIKE :name", ENTITY_ALIAS));
        }

        var priceMin = dto.getPriceMin();
        var priceMax = dto.getPriceMax();
        var hasPriceMin = Objects.nonNull(priceMin);
        var hasPriceMax = Objects.nonNull(priceMax);

        if (hasPriceMin && hasPriceMax) {
            parametersMap.put("priceMin", priceMin);
            parametersMap.put("priceMax", priceMin);
            conditions.add(String.format("%s.price BETWEEN :priceMin AND :priceMax", ENTITY_ALIAS));
        }
        else if (hasPriceMin) {
            parametersMap.put("priceMin", priceMin);
            conditions.add(String.format(":priceMin <= %s.price", ENTITY_ALIAS));
        }
        else if (hasPriceMax) {
            parametersMap.put("priceMax", priceMin);
            conditions.add(String.format("%s.price <= priceMax", ENTITY_ALIAS));
        }

        return parametersMap;
    }

}
