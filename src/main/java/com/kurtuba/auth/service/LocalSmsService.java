package com.kurtuba.auth.service;

import com.twilio.rest.verify.v2.service.Verification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class LocalSmsService implements ISMSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalSmsService.class);

    @Override
    public Object sendSMS(String recipientNumber, String sender, String messageContent) {
        LOGGER.info("Local SMS stub -> to={}, sender={}", recipientNumber, sender);
        return null;
    }

    @Override
    public Verification sendVerificationSMS(String recipient) {
        LOGGER.info("Local verification SMS stub -> to={}", recipient);
        return null;
    }

    @Override
    public Boolean checkVerification(String userMobile, String code) {
        LOGGER.info("Local verification check stub -> mobile={}", userMobile);
        return true;
    }

    @Override
    public Object deleteVerification(String sid) {
        LOGGER.info("Local verification delete stub -> sid={}", sid);
        return null;
    }
}
