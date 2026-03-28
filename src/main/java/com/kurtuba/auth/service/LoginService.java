package com.kurtuba.auth.service;

import com.kurtuba.auth.data.dto.TokensResponseDto;
import com.kurtuba.auth.data.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserTokenService userTokenService;
    private final AuthenticationService authenticationService;

    @Transactional
    public TokensResponseDto authenticateAndGetTokens(String emailMobile, String pass,
                                                      String registeredClientId, String registeredClientSecret) {
            // authenticate user and get tokens
            return userTokenService.validateRegisteredClientAndGetTokens(
                    authenticationService.authenticate(emailMobile, pass), registeredClientId, registeredClientSecret);
    }

    @Transactional
    public TokensResponseDto getTokensForUser(User user, String registeredClientId, String registeredClientSecret) {
        return userTokenService.validateRegisteredClientAndGetTokens(user, registeredClientId, registeredClientSecret);
    }


}
