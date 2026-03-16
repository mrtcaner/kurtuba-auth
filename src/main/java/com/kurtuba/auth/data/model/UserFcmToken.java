package com.kurtuba.auth.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * a user can have multiple fcm tokens
 * a fcm token can be used by a single user
 * a firebaseInstallationId can be used by a single user
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_fcm_token")
public class UserFcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    String userId;

    @NotBlank
    @Column(unique = true)
    String fcmToken;

    @NotBlank
    @Column(unique = true)
    String firebaseInstallationId; // to track user-device mapping

    @NotBlank
    String registeredClientId; // to track type of device(mobile, web)

    @NotNull
    Instant updatedAt;

}
