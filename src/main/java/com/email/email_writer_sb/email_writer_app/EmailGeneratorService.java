package com.email.email_writer_sb.email_writer_app;

import org.springframework.stereotype.Service;

@Service
public interface EmailGeneratorService {
    String generateEmailResponse(EmailRequest emailRequest);
}
