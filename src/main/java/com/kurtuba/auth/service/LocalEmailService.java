package com.kurtuba.auth.service;

import com.kurtuba.auth.data.model.EmailDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class LocalEmailService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalEmailService.class);

    @Override
    public void sendMultipartMail(EmailDetails details) {
        LOGGER.info("Local email stub -> to={}, subject={}", details.getRecipient(), details.getSubject());
    }
}
