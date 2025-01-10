package com.kurtuba.auth.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 *  For web clients with jwt in cookie
 */
@Data
@Builder
public class TokenRefreshWebRequestDto {

    @NotBlank
    String clientId;
    
    String clientSecret;
}
