package com.saynotohunger.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService 
{
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender javaMailSender;

    // Constructor Injection
    public EmailService(JavaMailSender javaMailSender) 
    {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmail(String to, String subject, String body) 
    {
        try
        {
            logger.debug("Preparing email");
            logger.debug("To: {}", to);
            logger.debug("Subject: {}", subject);

            /*SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);

                javaMailSender.send(message); */

            SimpleMailMessage message = new SimpleMailMessage();

            logger.info("MAIL_USERNAME at emailservice= {}", System.getenv("MAIL_USERNAME"));
            logger.info("MAIL_PASSWORD exists emailservice= {}", System.getenv("MAIL_PASSWORD") != null);

            message.setFrom("aishwaryapat932@gmail.com"); // Your verified Brevo sender
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);

           logger.info("Email sent successfully to {}", to);
        }
        catch (Exception e) 
        {
            logger.error("Email sending failed", e);
            
            e.printStackTrace();
        }
    }
}