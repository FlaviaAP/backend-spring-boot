package com.backendabstractmodel.demo.domain.repository;

import com.backendabstractmodel.demo.domain.entity.TextTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextTemplateRepository extends JpaRepository<TextTemplate, Integer> {
}
