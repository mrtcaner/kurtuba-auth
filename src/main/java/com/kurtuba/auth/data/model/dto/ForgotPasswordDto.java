package com.kurtuba.auth.data.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ForgotPasswordDto {

    @Email
    @NotEmpty
    private String email;
}