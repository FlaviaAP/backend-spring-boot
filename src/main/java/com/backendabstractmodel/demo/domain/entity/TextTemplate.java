package com.backendabstractmodel.demo.domain.entity;

import com.backendabstractmodel.demo.domain.entity.dbconfig.DatabaseMetadata;
import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Template")
@Entity
@Table(schema = DatabaseMetadata.SCHEMA, name = "tb_text_template")
public class TextTemplate extends BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template")
    private Integer id;

    @Column(name = "ds_template", nullable = false)
    private String content;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() || !super.equals(o)) return false;
        TextTemplate that = (TextTemplate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, content);
    }
}
