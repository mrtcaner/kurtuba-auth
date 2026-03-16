package com.kurtuba.auth.config.rateLimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurtuba.auth.data.dto.ResponseErrorDto;
import com.kurtuba.auth.data.enums.RateLimitPublicApi;
import com.kurtuba.auth.error.enums.ErrorEnum;
import com.kurtuba.auth.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.pattern.PathPatternParser;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "kurtuba.rate-limit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;
    private final PathPatternParser parser = new PathPatternParser();
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getServletPath();
        // 1. Find matching API config
        RateLimitPublicApi matchedApi = Arrays.stream(RateLimitPublicApi.values())
                                              .filter(api -> parser.parse(api.getPattern()).matches(
                                                      PathContainer.parsePath(path)))
                                              .findFirst()
                                              .orElse(null);

        if (matchedApi == null){
            System.out.println("path not matched:" + path);
            return true; // Not a rate-limited path
        }
        System.out.println("path matched:" + path);
        // 2. Resolve bucket and consume
        String clientIp = getClientIp(request);
        Bucket bucket = rateLimitingService.resolveBucket(clientIp, matchedApi);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            // 3. Handle rejection
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.setContentType("application/json");
            ResponseErrorDto errorDetails = ResponseErrorDto
                    .builder()
                    .code(ErrorEnum.TOO_MANY_REQUESTS.getCode())
                    .message(ErrorEnum.TOO_MANY_REQUESTS.getMessage())
                    .error(ErrorEnum.TOO_MANY_REQUESTS.getMessage())
                    .data(Map.of("retryAfterSeconds",waitForRefill))
                    .timestamp(Instant.now())
                    .build();

            response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
            return false;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return (xfHeader == null || xfHeader.isBlank()) ? request.getRemoteAddr() : xfHeader.split(",")[0].trim();
    }
}
