package com.kurtuba.adm.controller;

import com.kurtuba.adm.data.dto.TokenBlockDto;
import com.kurtuba.auth.data.model.UserToken;
import com.kurtuba.auth.service.UserTokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("adm")
public class TokenManagementController {

    final
    UserTokenService userTokenService;

    public TokenManagementController(UserTokenService userTokenService) {
        this.userTokenService = userTokenService;
    }

    @GetMapping("/token/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserToken>> getIssuedTokensOfUser(@PathVariable @NotEmpty String userId) {
        return ResponseEntity.ok(userTokenService.findAllByUserId(userId));
    }

    @PutMapping("/token/blocked")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<String>> unblockTokens(@Valid @RequestBody TokenBlockDto tokenBlockDto) {
        userTokenService.changeTokenBlockByJTI(tokenBlockDto.getTokenIds(), tokenBlockDto.isBlock());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/token/user/blocked/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<String>> blockUsersTokens(@PathVariable @NotEmpty String userId) {
        userTokenService.blockUsersTokens(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/token/user/blocked/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserToken>> getUsersBlockedTokens(@PathVariable @NotEmpty String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userTokenService.findAllByUserIdAndBlocked(userId, true));
    }

    @GetMapping("/token/blocked/{jti}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity getBlockedToken(@PathVariable @NotEmpty String jti) {
        return ResponseEntity.status(HttpStatus.OK).body(userTokenService.checkDBIfTokenIsBlockedByJTI(jti));
    }

}