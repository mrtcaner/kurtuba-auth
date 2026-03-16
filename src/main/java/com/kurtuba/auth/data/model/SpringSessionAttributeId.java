package com.kurtuba.auth.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class SpringSessionAttributeId implements Serializable {
    private static final long serialVersionUID = -3908506377962592722L;
    @Size(max = 36)
    @NotNull
    @Column(name = "session_primary_id", nullable = false, length = 36)
    private String sessionPrimaryId;

    @Size(max = 200)
    @NotNull
    @Column(name = "attribute_name", nullable = false, length = 200)
    private String attributeName;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        SpringSessionAttributeId entity = (SpringSessionAttributeId) o;
        return Objects.equals(this.sessionPrimaryId, entity.sessionPrimaryId) &&
               Objects.equals(this.attributeName, entity.attributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionPrimaryId, attributeName);
    }

}