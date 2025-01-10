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
public class TokensResponseDto {

    @NotBlank
    public String accessToken;

    @NotBlank
    public String refreshToken;
}
