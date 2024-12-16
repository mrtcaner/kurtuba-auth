package com.kurtuba.auth.controller;


import com.kurtuba.auth.data.model.dto.TokensDto;
import com.kurtuba.auth.error.enums.ErrorEnum;
import com.kurtuba.auth.error.exception.BusinessLogicException;
import com.kurtuba.auth.service.UserTokenService;
import com.kurtuba.auth.utils.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("auth")
public class RefreshTokenController {

    @Value("${kurtuba.web-client.cookie.max-age.seconds}")
    private int webClientCookieMaxAgeValiditySeconds;

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

    @PostMapping("/web/token/refresh")
    public ResponseEntity refreshWebClientTokens(HttpServletRequest request) {
        // find the jwt cookie
        String jwt = request.getCookies() == null ? null : Arrays
                .stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("jwt"))
                .map(cookie -> cookie.getValue())
                .findFirst()
                .orElse(null);
        if(jwt == null){
            throw new BusinessLogicException(ErrorEnum.AUTH_REFRESH_TOKEN_INVALID);
        }

        ResponseCookie cookie = ResponseCookie.from("jwt", userTokenService.refreshWebClientTokens(jwt))
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(webClientCookieMaxAgeValiditySeconds)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("");
    }
}
