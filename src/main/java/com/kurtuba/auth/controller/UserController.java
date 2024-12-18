package com.kurtuba.auth.controller;


import com.kurtuba.auth.data.model.AuthoritiesEnum;
import com.kurtuba.auth.data.model.JWTClaimsEnum;
import com.kurtuba.auth.data.model.dto.PasswordChangeDto;
import com.kurtuba.auth.data.model.dto.PasswordResetDto;
import com.kurtuba.auth.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("user")
public class UserController {

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * this method is for internal use only
     * token must have SERVICE in scope claim
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_SERVICE')")
    public ResponseEntity getUserById(@PathVariable @NotEmpty String id) {
        return ResponseEntity.status(HttpStatus.OK_200).body(userService.getUserById(id));
    }

    /**
     * users with a valid token can access
     * todo only certain info must be shared through a DTO
     * @param principal
     * @return
     */
    @GetMapping("/info")
    public ResponseEntity getUserInfo(JwtAuthenticationToken principal) {
        if(principal == null){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED_401).body("");
        }
        if(principal.getAuthorities().contains(JWTClaimsEnum.SCOPE.name() + "_" +AuthoritiesEnum.SERVICE.name())){
            // SERVICEs are not users
            return ResponseEntity.status(HttpStatus.BAD_REQUEST_400).body("");
        }
        return ResponseEntity.status(HttpStatus.OK_200).body(userService.getUserById(principal.getName()));
    }

    @PutMapping("/password")
    public ResponseEntity changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto,Principal principal) {
        userService.changePassword(passwordChangeDto, principal.getName());
        return ResponseEntity.status(HttpStatus.OK_200).body("");
    }

    //todo must return a page which user can change password
    @PutMapping("/password/reset/link/{code}")
    public ResponseEntity resetPasswordByLink(@Valid @PathVariable String code) {
        //userService.resetPassword(passwordResetDto);
        return ResponseEntity.status(HttpStatus.OK_200).body("");
    }

    @PutMapping("/password/reset/code")
    public ResponseEntity resetPasswordByCode(@Valid @RequestBody PasswordResetDto passwordResetDto) {
        userService.resetPasswordByCode(passwordResetDto);
        return ResponseEntity.status(HttpStatus.OK_200).body("");
    }

    @PostMapping("/password/reset/code/{usernameEmail}")
    public ResponseEntity requestPasswordResetByCode(@NotEmpty @PathVariable String usernameEmail) {
        userService.requestResetPassword(usernameEmail, true);
        return ResponseEntity.status(HttpStatus.OK_200).body("");
    }

    @PostMapping("/password/reset/link/{usernameEmail}")
    public ResponseEntity requestPasswordResetByLink(@NotEmpty @PathVariable String usernameEmail) {
        userService.requestResetPassword(usernameEmail, false);
        return ResponseEntity.status(HttpStatus.OK_200).body("");
    }

}
