package com.kurtuba.auth.controller;


import com.kurtuba.auth.data.model.dto.TokensDto;
import com.kurtuba.auth.service.UserTokenService;
import com.kurtuba.auth.utils.TokenUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class RefreshTokenController {

    private static final int TOKEN_COOKIE_MAX_AGE_SECONDS = 7776000;

    final
    UserTokenService userTokenService;

    final
    TokenUtils tokenUtils;

    public RefreshTokenController(UserTokenService userTokenService, TokenUtils tokenUtils) {
        this.userTokenService = userTokenService;
        this.tokenUtils = tokenUtils;
    }

    /**
     * Refresh token is rotated, can only be used once
     *
     * @param tokensDto
     * @return
     */
    @PostMapping("/token/refresh")
    public ResponseEntity refreshTokens(@Valid @RequestBody TokensDto tokensDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userTokenService.refreshUserTokens(tokensDto));
    }
}
