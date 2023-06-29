package com.backendabstractmodel.demo.domain.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductPaginationDTO extends PaginationDTO {

    private String name;
    private Double priceMin;
    private Double priceMax;

}
