package com.kurtuba.auth.data.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokensDto {
    @NotEmpty
    String accessToken;
    @NotEmpty
    String refreshToken;
}
