package com.kurtuba.auth.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "spring_session_attributes")
public class SpringSessionAttribute {
    @EmbeddedId
    private SpringSessionAttributeId id;

    @MapsId("sessionPrimaryId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "session_primary_id", nullable = false)
    private SpringSession sessionPrimary;

    @NotNull
    @Column(name = "attribute_bytes", nullable = false)
    private byte[] attributeBytes;

}