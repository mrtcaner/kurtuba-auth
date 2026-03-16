package com.kurtuba.auth.service;

import com.twilio.rest.verify.v2.service.Verification;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class LocalSmsService implements ISMSService {

    @Override
    public Object sendSMS(String recipientNumber, String sender, String messageContent) {
        System.out.println("LocalSmsService.sendSMS -> to=" + recipientNumber + ", sender=" + sender);
        return null;
    }

    @Override
    public Verification sendVerificationSMS(String recipient) {
        System.out.println("LocalSmsService.sendVerificationSMS -> to=" + recipient);
        return null;
    }

    @Override
    public Boolean checkVerification(String userMobile, String code) {
        System.out.println("LocalSmsService.checkVerification -> mobile=" + userMobile + ", code=" + code);
        return true;
    }

    @Override
    public Object deleteVerification(String sid) {
        System.out.println("LocalSmsService.deleteVerification -> sid=" + sid);
        return null;
    }
}
