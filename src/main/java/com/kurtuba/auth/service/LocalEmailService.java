package com.kurtuba.auth.service;

import com.kurtuba.auth.data.model.EmailDetails;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class LocalEmailService implements EmailService {

    @Override
    public void sendMultipartMail(EmailDetails details) {
        System.out.println("LocalEmailService -> to=" + details.getRecipient() + ", subject=" + details.getSubject());
    }
}
