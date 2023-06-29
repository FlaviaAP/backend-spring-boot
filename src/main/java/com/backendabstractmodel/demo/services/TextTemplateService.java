package com.backendabstractmodel.demo.services;

import com.backendabstractmodel.demo.domain.entity.TextTemplate;
import com.backendabstractmodel.demo.domain.enumeration.TemplateTypeEnum;
import com.backendabstractmodel.demo.domain.repository.TextTemplateRepository;
import com.backendabstractmodel.demo.exceptions.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TextTemplateService {

    @Autowired
    private TextTemplateRepository repository;


    public TextTemplate getByType(TemplateTypeEnum typeEnum) {
        return repository.findById(typeEnum.getId())
            .orElseThrow(() -> new ObjectNotFoundException("Template type={"+typeEnum+"} NOT FOUND"));
    }

}
