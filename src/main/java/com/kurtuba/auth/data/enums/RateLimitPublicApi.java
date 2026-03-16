package com.kurtuba.auth.data.enums;

import io.github.bucket4j.Bandwidth;

import java.time.Duration;

public enum RateLimitPublicApi {
    REGISTRATION("/registration/**", 30, Duration.ofMinutes(15)),
    AUTH("/auth/**", 50, Duration.ofMinutes(10)),
    PASSWORD_RESET("/user/password/reset/**", 5, Duration.ofHours(1)),
    SMS("/sms/**", 5, Duration.ofMinutes(15)),
    VERIFICATION("/user/email/verification/link/**", 15, Duration.ofMinutes(15));

    private final String pattern;
    private final int capacity;
    private final Duration refill;

    RateLimitPublicApi(String pattern, int capacity, Duration refill) {
        this.pattern = pattern;
        this.capacity = capacity;
        this.refill = refill;
    }

    public Bandwidth getLimit() {
        return Bandwidth.builder()
                        .capacity(capacity)
                        .refillIntervally(capacity, refill)
                        .build();
    }

    public String getPattern() { return pattern; }
}