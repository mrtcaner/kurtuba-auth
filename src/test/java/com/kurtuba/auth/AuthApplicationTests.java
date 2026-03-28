package com.kurtuba.auth;

import com.kurtuba.auth.utils.TokenUtils;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@Import(AuthApplicationTests.TestConfig.class)
public class AuthApplicationTests {

    @Test
    public void contextLoads() {
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        TokenUtils tokenUtils() throws Exception {
            TokenUtils tokenUtils = Mockito.mock(TokenUtils.class);
            PublicJsonWebKey jwk = RsaJwkGenerator.generateJwk(2048);
            jwk.setKeyId("test-key");
            Mockito.when(tokenUtils.getAllSigningKeys()).thenReturn(List.of(jwk));
            return tokenUtils;
        }
    }
}
