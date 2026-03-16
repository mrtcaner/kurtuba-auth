package com.kurtuba.auth.data.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "spring_session")
public class SpringSession {
    @Id
    @Size(max = 36)
    @Column(name = "primary_id", nullable = false, length = 36)
    private String primaryId;

    @Size(max = 36)
    @NotNull
    @Column(name = "session_id", nullable = false, length = 36)
    private String sessionId;

    @NotNull
    @Column(name = "creation_time", nullable = false)
    private Long creationTime;

    @NotNull
    @Column(name = "last_access_time", nullable = false)
    private Long lastAccessTime;

    @NotNull
    @Column(name = "max_inactive_interval", nullable = false)
    private Integer maxInactiveInterval;

    @NotNull
    @Column(name = "expiry_time", nullable = false)
    private Long expiryTime;

    @Size(max = 100)
    @Column(name = "principal_name", length = 100)
    private String principalName;

}