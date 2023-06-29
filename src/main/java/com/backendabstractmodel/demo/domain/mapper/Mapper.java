package com.backendabstractmodel.demo.domain.mapper;

import com.backendabstractmodel.demo.domain.dto.BaseDTO;
import com.backendabstractmodel.demo.domain.entity.BaseEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Mapper<E extends BaseEntity<?>, D extends BaseDTO<?>> {

    private static final ModelMapper MAPPER = new ModelMapper();
    private Class<? extends BaseEntity<?>> entityClass;
    private Class<? extends BaseDTO<?>> dtoClass;

    public Mapper(Class<? extends BaseEntity<?>> entityClass, Class<? extends BaseDTO<?>> dtoClass) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public String getEntityName() {
        return this.entityClass.getSimpleName();
    }

    public D entityToDTO(E entity) {
        return (D) converter(entity, dtoClass);
    }

    public E dtoToEntity(D dto) {
        return (E) converter(dto, entityClass);
    }

    private <S, T> T converter(S source, Class<T> target) {
        return MAPPER.map(source, target);
    }

    public List<D> listEntityToDTO(List<E> list) {
        return list.stream()
            .map(this::entityToDTO)
            .collect(Collectors.toList());
    }

    public List<E> listDTOToEntity(List<D> list) {
        return list.stream()
            .map(this::dtoToEntity)
            .collect(Collectors.toList());
    }

    protected byte[] linkedHashMapToByteArray(LinkedHashMap<String, Integer> map) {
        return ArrayUtils.toPrimitive(
            map.values().stream()
                .map(value -> (byte)(int)value)
                .collect(Collectors.toList())
                .toArray(Byte[]::new)
        );
    }

}
