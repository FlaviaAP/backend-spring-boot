package com.backendabstractmodel.demo.domain.dto.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class PaginationDTO {

    protected Integer pageIndex;
    protected Integer pageSize;
    protected String sortField = "name";
    protected Boolean isAscSortMode = true;
    protected String name;


    public Pageable toPageable() {
        Sort sort = Sort.by(sortField);
        sort = isAscSortMode ? sort.ascending() : sort.descending();
        return PageRequest.of(getPageIndex(), getPageSize(), sort);
    }
}
