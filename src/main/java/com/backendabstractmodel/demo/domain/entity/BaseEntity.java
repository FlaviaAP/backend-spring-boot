package com.backendabstractmodel.demo.domain.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public abstract class BaseEntity<ID extends Serializable> implements Serializable {

    private static final long serialVersionID = 1L;

    public abstract ID getId();

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }

        BaseEntity<?> other = (BaseEntity<?>) obj;

        EqualsBuilder eb = new EqualsBuilder();
        eb.append(getId(), other.getId());
        return eb.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getId()).toHashCode();
    }

}
