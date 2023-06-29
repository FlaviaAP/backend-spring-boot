package com.backendabstractmodel.demo.domain.entity;

import com.backendabstractmodel.demo.domain.entity.dbconfig.ColumnDefinition;
import com.backendabstractmodel.demo.domain.entity.dbconfig.DatabaseMetadata;
import com.backendabstractmodel.demo.domain.entity.dbconfig.HibernateUtil;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ApiModel(value = "Product")
@Entity
@Table(schema = DatabaseMetadata.SCHEMA, name = "tb_product")
public class Product extends BaseEntity<UUID> {

    @Id
    @GeneratedValue(generator = HibernateUtil.UUID_GENERATOR)
    @GenericGenerator(name = HibernateUtil.UUID_GENERATOR, strategy = HibernateUtil.UUID_GENERATOR_STRATEGY)
    @Column(name = "uuid_product", columnDefinition = ColumnDefinition.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Double price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() || !super.equals(o)) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
