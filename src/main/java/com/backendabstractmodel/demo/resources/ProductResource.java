package com.backendabstractmodel.demo.resources;

import com.backendabstractmodel.demo.domain.dto.ProductDTO;
import com.backendabstractmodel.demo.domain.dto.pagination.ProductPaginationDTO;
import com.backendabstractmodel.demo.domain.dto.search_result.PageResult;
import com.backendabstractmodel.demo.exceptions.BusinessException;
import com.backendabstractmodel.demo.resources.util.RoleControl;
import com.backendabstractmodel.demo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductResource extends BaseResource<ProductDTO, UUID> {

    @Autowired
    private ProductService service;

    @Override
    @RolesAllowed(RoleControl.PRODUCT_ADD)
    protected ProductDTO create(ProductDTO dto) {
        return service.create(dto);
    }

    @Override
    @RolesAllowed(RoleControl.PRODUCT_EDIT)
    protected ProductDTO update(ProductDTO dto) {
        return service.update(dto);
    }

    @Override
    @RolesAllowed(RoleControl.PRODUCT_VIEW)
    protected ProductDTO getById(UUID id) throws BusinessException {
        return service.getById(id);
    }

    @Override
    @RolesAllowed(RoleControl.PRODUCT_DELETE)
    protected void delete(UUID id) {
        service.deleteById(id);
    }

    @Override
    @RolesAllowed(RoleControl.PRODUCT_DELETE)
    protected void deleteAll(List<UUID> ids) {
        service.deleteAllByIds(ids);
    }

    @RolesAllowed(RoleControl.PRODUCT_LIST)
    @PostMapping(value = "paged-list", produces = JSON)
    protected PageResult<ProductDTO> getAllByPage(@RequestBody ProductPaginationDTO dto) {
        return service.getPagedList(dto);
    }
}
