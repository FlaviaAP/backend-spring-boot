package com.backendabstractmodel.demo.domain.enumeration;

import lombok.Getter;

@Getter
public enum TemplateTypeEnum {

    CONFIRM_EMAIL(1)
    ;

    private Integer id;

    TemplateTypeEnum(Integer id) {
        this.id = id;
    }

}
