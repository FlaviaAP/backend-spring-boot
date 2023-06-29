package com.backendabstractmodel.demo.resources;

import com.backendabstractmodel.demo.domain.dto.DTO;
import com.backendabstractmodel.demo.exceptions.BusinessException;
import com.backendabstractmodel.demo.exceptions.ObjectNotFoundException;
import com.backendabstractmodel.demo.resources.util.ResponseType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public abstract class BaseResource<D extends DTO, K> {

    protected static final String JSON = ResponseType.JSON;

    /* ******************************************************************* */
    @RequestMapping(
        method = { RequestMethod.POST },
        produces = { JSON }
    )
    public ResponseEntity<D> doCreate(@RequestBody D dto) {
        return ResponseEntity.ok(create(dto));
    }

    protected abstract D create(D dto);

    /* ******************************************************************* */
    @RequestMapping(
        method = { RequestMethod.PUT },
        produces = { JSON }
    )
    public ResponseEntity<D> doUpdate(@RequestBody D dto) {
        return ResponseEntity.ok(update(dto));
    }

    protected abstract D update(D dto);


    /* ******************************************************************* */
    @RequestMapping(
        value = {"/{id}"},
        method = { RequestMethod.GET },
        produces = { JSON }
    )
    public ResponseEntity<D> doGetById(@PathVariable("id") K id) throws BusinessException {
        try{
            return ResponseEntity.ok(getById(id));
        }
        catch (ObjectNotFoundException e) {
            return (ResponseEntity<D>) ResponseEntity.notFound();
        }
    }

    protected abstract D getById(K id) throws BusinessException;

    /* ******************************************************************* */
    @RequestMapping(
        method = { RequestMethod.GET },
        value = { "/list" },
        produces = { JSON }
    )
    public ResponseEntity<List<D>> doGetAll() throws BusinessException {
        return ResponseEntity.ok(getAll());
    }

    protected List<D> getAll() throws BusinessException {
        return null; // child classes could implement
    }

    /* ******************************************************************* */
    @RequestMapping(
        value = { "/{id}" },
        method = {RequestMethod.DELETE },
        produces = { JSON }
    )
    public void doDelete(@PathVariable("id") K id) {
        delete(id);
    }

    protected abstract void delete(K id);

    /* ******************************************************************* */
    @RequestMapping(
        value = { "/list/{ids}" },
        method = {RequestMethod.DELETE },
        produces = { JSON }
    )
    public void doDeleteAll(@PathVariable("ids") List<K> ids) {
        deleteAll(ids);
    }

    protected abstract void deleteAll(List<K> ids);

}
