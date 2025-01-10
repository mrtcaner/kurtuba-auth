package com.kurtuba.auth.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginCredentialsDto {
    @NotBlank
    String emailUsername;

    @NotBlank
    String password;

    String clientId;

    String clientSecret;
}
