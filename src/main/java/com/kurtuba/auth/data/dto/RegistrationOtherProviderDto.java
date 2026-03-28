package com.kurtuba.auth.data.dto;

import com.kurtuba.auth.data.enums.AuthProviderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationOtherProviderDto {

    @NotNull
    AuthProviderType provider;
    @NotBlank
    String providerClientId;
    String token;
    String authorizationCode;
    String redirectUri;

    String registeredClientId;

    String registeredClientSecret;

    @NotBlank
    private String languageCode;

    @NotBlank
    private String countryCode;
}
