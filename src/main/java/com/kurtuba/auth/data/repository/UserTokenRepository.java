package com.kurtuba.auth.data.repository;

import com.kurtuba.auth.data.model.UserToken;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserTokenRepository extends CrudRepository<UserToken, String> {

    Optional<UserToken> findByJtiAndBlockedAndRefreshTokenExpAfter(String jti, boolean blocked, LocalDateTime exp);

}
