package com.kurtuba.auth.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FcmTokenUpsertRequestDto {

    @NotBlank
    String fcmToken;

    @NotBlank
    String firebaseInstallationId;

}
