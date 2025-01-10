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
public class AccountActivationDto {

    @NotBlank
    String emailMobile;
    @NotBlank
    String code;

    String clientId;
    String clientSecret;
}
