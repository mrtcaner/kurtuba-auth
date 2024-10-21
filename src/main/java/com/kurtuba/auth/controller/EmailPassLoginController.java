package com.kurtuba.auth.controller;

import com.kurtuba.auth.data.model.ClientType;
import com.kurtuba.auth.data.model.dto.LoginCredentialsDto;
import com.kurtuba.auth.data.model.dto.TokenDto;
import com.kurtuba.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailPassLoginController {

    @Autowired
    UserService userService;

    @PostMapping("/auth/login")
    @ResponseBody
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginCredentialsDto loginCredentials){
        return ResponseEntity.status(HttpStatus.OK).body(userService.autheticate(loginCredentials.getEmailUsername(),loginCredentials.getPass(), loginCredentials.getClientType()));
    }
}
