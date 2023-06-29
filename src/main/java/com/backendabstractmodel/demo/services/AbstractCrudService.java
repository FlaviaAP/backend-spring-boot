package com.backendabstractmodel.demo.services;

import com.backendabstractmodel.demo.domain.dto.BaseDTO;
import com.backendabstractmodel.demo.domain.dto.search_result.PageResult;
import com.backendabstractmodel.demo.domain.entity.BaseEntity;
import com.backendabstractmodel.demo.domain.mapper.Mapper;
import com.backendabstractmodel.demo.enumeration.MessageEnum;
import com.backendabstractmodel.demo.exceptions.BusinessException;
import com.backendabstractmodel.demo.exceptions.ObjectNotFoundException;
import com.backendabstractmodel.demo.exceptions.UnexpectedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class AbstractCrudService<K extends Serializable, E extends BaseEntity<K>, D extends BaseDTO<K>> {

    private JpaRepository<E, K> repositoryRef;
    protected Mapper<E, D> mapper;

    protected abstract void postConstructorInit();

    protected String getEntityName() {
        return mapper.getEntityName();
    }


    @PostConstruct
    public void init() {
        setMapper();
        postConstructorInit();

        if(Objects.isNull(repositoryRef)) {
            log.error("Error: Repository was not setted by " + getEntityName());
            throw new NullPointerException("Error: Repository was not setted by " + getEntityName());
        }
    }

    private void setMapper() {
        var types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
        Class<E> entityClass = ((Class<E>) types[1]);
        Class<D> dtoClass = ((Class<D>) types[2]);
        this.mapper = new Mapper<>(entityClass, dtoClass);
    }

    public void initRefs(JpaRepository<E, K> repositoryRef) {
        this.repositoryRef = repositoryRef;
    }

    public void initRefs(JpaRepository<E, K> repositoryRef, Mapper<E,D> mapper) {
        this.repositoryRef = repositoryRef;
        this.mapper = mapper;
    }


    public Mapper<E,D> getMapper() {
        return this.mapper;
    }

    /* Insert */

    protected E createEntity(E entity) {
        return repositoryRef.save(entity);
    }

    @Transactional
    public D create(D dto) {
        try {
            checkIdForCreate(dto);
            validateAndPrepareDTOForCreate(dto);

            E entity = mapper.dtoToEntity(dto);
            entity = createEntity(entity);
            return mapper.entityToDTO(entity);
        }
        catch (ObjectNotFoundException | BusinessException | AccessDeniedException e) {
            throw e;
        }
        catch (Exception e) {
            throw handleCreateUnexpectedError(e);
        }
    }

    protected void checkIdForCreate(D dto) {
        if(Objects.nonNull(dto.getId())) {
            throw new IllegalArgumentException(MessageEnum.CAN_NOT_INSERT_WITH_ID.toString());
        }
    }

    protected void validateAndPrepareDTOForCreate(D dto){
        // This is intentional - could be implemented by child classes
    }

    /* Update */

    protected E updateEntity(E entity) {
        return repositoryRef.save(entity);
    }

    @Transactional
    public D update(D dto) {
        K id = null;
        try {
            id = dto.getId();
            checkIdForUpdate(dto);
            if (!dto.isValidForSave()) {
                throw new BusinessException(MessageEnum.INVALID_DATA_FOR_SAVE);
            }
            validateAndPrepareDTOForUpdate(dto);

            E entity = mapper.dtoToEntity(dto);
            entity = updateEntity(entity);
            return mapper.entityToDTO(entity);
        }
        catch (ObjectNotFoundException | BusinessException | AccessDeniedException e) {
            throw e;
        }
        catch (Exception e) {
            throw handleUpdateUnexpectedError(e, id);
        }
    }

    protected void checkIdForUpdate(D dto) {
        if(Objects.isNull(dto.getId())) {
            throw new NullPointerException(MessageEnum.CAN_NOT_UPDATE_WITHOUT_ID.toString());
        }
    }

    protected void validateAndPrepareDTOForUpdate(D dto) {
        // This is intentional - could be implemented by child classes
    }

    /* check duplicate for insert/update*/

    protected void checkDuplicateByNameForInsert(Optional<K> foundedIdByName) {
        checkDuplicateForInsert(foundedIdByName, MessageEnum.ALREADY_EXISTS_REGISTER_WITH_SAME_NAME);
    }

    protected void checkDuplicateForInsert(Optional<K> foundedIdBySomeAttribute, MessageEnum messageEnum) {
        if(foundedIdBySomeAttribute.isPresent()) {
            throw new BusinessException(messageEnum);
        }
    }

    protected void checkDuplicateByNameForUpdate(Optional<K> foundedIdByName, K dtoId) {
        checkDuplicateForUpdate(foundedIdByName, dtoId, MessageEnum.ALREADY_EXISTS_REGISTER_WITH_SAME_NAME);
    }

    protected void checkDuplicateForUpdate(Optional<K> foundedIdBySomeAttribute, K dtoId, MessageEnum messageEnum) {
        if(foundedIdBySomeAttribute.isPresent() && !dtoId.equals(foundedIdBySomeAttribute.get())) {
            throw new BusinessException(messageEnum);
        }
    }

    /* After commit */

    protected void registerAfterSaveCommitAction(D dtoBeforeSave) {
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization(){
                @Override
                public void afterCommit(){
                    afterSaveCommit(dtoBeforeSave);
                }
            }
        );
    }

    protected void afterSaveCommit(D dtoBeforeSave) {
        // This is intentional - child class could implement your own version
    }

    /* Get */

    protected E getEntityById(K id) {
        return repositoryRef.findById(id).orElseThrow(ObjectNotFoundException::new);
    }

    public D getById(K id) {
        return mapper.entityToDTO( getEntityById(id) );
    }

    @Transactional
    public List<D> getAll() {
        return mapper.listEntityToDTO(repositoryRef.findAll());
    }

    public List<D> getAllByIds(List<K> ids) {
        return mapper.listEntityToDTO( repositoryRef.findAllById(ids) );
    }


    protected PageResult<D> toPageResult(Page<E> entitiesPage) {
        return new PageResult<>(
            entitiesPage.getTotalElements(),
            mapper.listEntityToDTO(entitiesPage.getContent())
        );
    }

    /* Delete */

    @Transactional
    public void deleteById(K id) {
        try {
            deleteByIdAndDependencies(id);
        }
        catch (ObjectNotFoundException | BusinessException | AccessDeniedException e) {
            throw e;
        }
        catch (EmptyResultDataAccessException e) {
            log.error("Error at delete {} by id={}: NOT FOUND", getEntityName(), id);
            throw new ObjectNotFoundException(MessageEnum.DELETE_ERROR_NO_RESULT_FOUND);
        }
        catch (Exception e) {
            throw handleUnexpectedDeleteError(e, id);
        }
    }

    protected void deleteByIdAndDependencies(K id) {
        repositoryRef.deleteById(id);
    }

    protected E getByIdAndDelete(K id) {
        E entity = repositoryRef.getById(id);
        repositoryRef.deleteById(id);
        return entity;
    }

    @Transactional
    public void deleteAllByIds(List<K> ids) {
        try {
            deleteAllAndDependencies(ids);
        }
        catch (ObjectNotFoundException | BusinessException | AccessDeniedException e) {
            throw e;
        }
        catch (EmptyResultDataAccessException e) {
            log.error("Error at delete {} by ids={}", getEntityName(), ids);
            throw new ObjectNotFoundException(MessageEnum.DELETE_ERROR_NO_RESULT_FOUND);
        }
        catch (Exception e) {
            throw handleUnexpectedDeleteError(e, ids);
        }
    }

    protected void deleteAllAndDependencies(List<K> ids) {
        repositoryRef.deleteAllById(ids);
    }

    /* Exceptions */

    protected UnexpectedException handleCreateUnexpectedError(Exception e) {
        log.error("Error at create {}", getEntityName());
        return new UnexpectedException(MessageEnum.SAVE_ERROR, e);
    }

    protected UnexpectedException handleUpdateUnexpectedError(Exception e, K id) {
        log.error("Error at update {} with id={}", getEntityName(), id);
        return new UnexpectedException(MessageEnum.SAVE_ERROR, e);
    }

    protected UnexpectedException handleUnexpectedDeleteError(Exception e, K id) {
        log.error("Error at delete {} by id={}", getEntityName(), id);
        return new UnexpectedException(MessageEnum.DELETE_ERROR, e);
    }

    protected UnexpectedException handleUnexpectedDeleteError(Exception e, List<K> ids) {
        log.error("Error at delete {} by ids={}", getEntityName(), ids);
        return new UnexpectedException(MessageEnum.DELETE_ERROR, e);
    }

}
