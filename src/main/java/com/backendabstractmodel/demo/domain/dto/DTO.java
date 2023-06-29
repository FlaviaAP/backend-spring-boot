package com.backendabstractmodel.demo.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface DTO {

    @JsonIgnore
    boolean isValidForSave();

}
