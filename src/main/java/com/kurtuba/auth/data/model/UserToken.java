package com.kurtuba.auth.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_token")
public class UserToken {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotEmpty
    private String userId;

    @NotEmpty
    private String refreshToken;

    @NotNull
    private LocalDateTime refreshTokenExp;

    @NotEmpty
    private String jti;

    @NotEmpty
    private String clientId;

    private boolean blocked;

    @NotNull
    private LocalDateTime createdDate;

    @NotNull
    private LocalDateTime expirationDate;


}
