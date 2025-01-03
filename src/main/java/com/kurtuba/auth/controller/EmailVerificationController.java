package com.kurtuba.auth.controller;

import com.kurtuba.auth.data.dto.EmailVerificationDto;
import com.kurtuba.auth.data.dto.ResultPageDto;
import com.kurtuba.auth.error.enums.ErrorEnum;
import com.kurtuba.auth.error.exception.BusinessLogicException;
import com.kurtuba.auth.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("auth")
public class EmailVerificationController {

    final
    UserService userService;

    public EmailVerificationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/email/verification/link/{linkParam}")
    private ModelAndView verifyEmailByLink(@NotEmpty @PathVariable String linkParam) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            userService.verifyEmailByLink(linkParam);
            modelAndView.setViewName("genericResult.html");//sucess
            modelAndView.addAllObjects(ResultPageDto.builder()
                    .success(true)
                    .title("Congratulations!")
                    .message1("You can now log in to your account with your email address")
                    .build().toMap());
        } catch (BusinessLogicException ex) {
            modelAndView.setViewName("genericResult.html");//failure
            modelAndView.addAllObjects(ResultPageDto.builder()
                    .success(false)
                    .title("Verification Failed!")
                    .message1(ex.getMessage())
                    .message2("Try logging in to your account to request a new verification link")
                    .build().toMap());
        }
        return modelAndView;
    }

    @PostMapping("/email/verification/code")
    private ResponseEntity verifyEmailByCode(@Valid @RequestBody EmailVerificationDto verificationDto) {
        try {
            userService.verifyEmailByCode(verificationDto.getUserMetaChangeId(), verificationDto.getCode());
        } catch (BusinessLogicException e) {
            if (ErrorEnum.USER_META_CHANGE_CODE_MISMATCH.getCode().equals(e.getErrorCode())) {
                userService.updateEmailChangeTryCount(verificationDto);
            }
            throw e;
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(org.eclipse.jetty.http.HttpStatus.OK_200))
                .body("");
    }

    @PutMapping("/email/registration/verification/code/{email}")
    private ResponseEntity resendAccountActivationCode(@NotEmpty @PathVariable String email) {
        userService.sendAccountActivationMail(email, true);
        return ResponseEntity.status(HttpStatusCode.valueOf(org.eclipse.jetty.http.HttpStatus.OK_200)).body("success");
    }

    @PutMapping("/email/registration/verification/link/{email}")
    private ResponseEntity resendAccountActivationLink(@NotEmpty @PathVariable String email) {
        userService.sendAccountActivationMail(email, false);
        return ResponseEntity.status(HttpStatusCode.valueOf(HttpStatus.OK_200)).body("success");
    }
}