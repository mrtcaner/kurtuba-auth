package com.kurtuba.adm.controller;

import com.kurtuba.auth.data.model.UserToken;
import com.kurtuba.auth.service.UserTokenService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth/adm")
public class AdmUserController {

    final
    UserTokenService userTokenService;

    public AdmUserController(UserTokenService userTokenService) {
        this.userTokenService = userTokenService;
    }

    @GetMapping("/user/token/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserToken>> getIssuedTokensOfUser(@PathVariable @NotBlank String userId){
        return ResponseEntity.ok(userTokenService.findAllByUserId(userId));
    }

}
