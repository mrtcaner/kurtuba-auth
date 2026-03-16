package com.kurtuba.auth.config.rateLimit;

import com.kurtuba.auth.data.enums.RateLimitPublicApi;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "kurtuba.rate-limit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class Bucket4jRateLimitConfigurer implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Map all patterns from your Enum
        String[] patterns = Arrays.stream(RateLimitPublicApi.values())
                                  .map(RateLimitPublicApi::getPattern)
                                  .toArray(String[]::new);

        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns(patterns)
                // Always a good idea to exclude static assets just in case
                .excludePathPatterns("/favicon.ico", "/error", "/static/**");
    }
}
