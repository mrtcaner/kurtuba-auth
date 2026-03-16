package com.kurtuba.auth.data.repository;

import com.kurtuba.auth.data.model.UserFcmToken;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Optional;

public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, String> {
    List<UserFcmToken> findByUserId(String userId);

    void deleteByUserIdAndFirebaseInstallationId(String userId, String firebaseInstallationId);

    Optional<UserFcmToken> findByFcmToken(String fcmToken);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "2000")})
    @Query("SELECT u FROM UserFcmToken u WHERE u.firebaseInstallationId = :installationId")
    Optional<UserFcmToken> findByInstallationIdForUpdate(String installationId);
}
