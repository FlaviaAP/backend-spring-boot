package com.backendabstractmodel.demo.domain.repository.custom;

import com.backendabstractmodel.demo.domain.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AbstractCustomRepository<E extends BaseEntity<?>> {

    protected Page<E> findByPage(
        String entityAlias,
        Pageable pageable,
        EntityManager entityManager,
        StringBuilder queryBuilder, 
        StringBuilder countQueryBuilder,
        Map<String, Object> parametersMap,
        List<String> conditions
    ) {
        if(!conditions.isEmpty()) {
            String whereStatement = " WHERE " + String.join(" AND ", conditions);
            queryBuilder.append(whereStatement);
            countQueryBuilder.append(whereStatement);
        }

        var order = pageable.getSort().get().findFirst().orElse(null);
        if(Objects.nonNull(order)) {
            queryBuilder.append(
                String.format(" ORDER BY %s.%s %s", entityAlias, order.getProperty(), order.getDirection())
            );
        }

        var query = entityManager.createQuery(queryBuilder.toString());
        var queryCount = entityManager.createQuery(countQueryBuilder.toString());

        for (var parameter : parametersMap.entrySet()) {
            query.setParameter(parameter.getKey(), parameter.getValue());
            queryCount.setParameter(parameter.getKey(), parameter.getValue());
        }

        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());

        long count = (long) queryCount.getSingleResult();

        return new PageImpl<E>(query.getResultList(), pageable, count);
    }
    
}
