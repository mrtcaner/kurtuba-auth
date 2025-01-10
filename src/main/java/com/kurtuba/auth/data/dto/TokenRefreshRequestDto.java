package com.kurtuba.auth.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * For all clients except web clients with jwt in cookie
 */
@Data
@Builder
public class TokenRefreshRequestDto {

    @NotBlank
    String accessToken;

    @NotBlank
    String refreshToken;

    @NotBlank
    String clientId;

    String clientSecret;
}
