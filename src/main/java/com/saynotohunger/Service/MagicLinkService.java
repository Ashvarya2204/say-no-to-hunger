package com.saynotohunger.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.saynotohunger.Entity.LoginToken;
import com.saynotohunger.Entity.User;
import com.saynotohunger.dao.LoginTokenRepository;
import com.saynotohunger.dao.UserRepository;
import com.saynotohunger.Exception.BusinessException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;     
import org.springframework.mail.javamail.JavaMailSender; 

@Service
public class MagicLinkService 
{
    private static final Logger logger = LoggerFactory.getLogger(MagicLinkService.class);

    private final UserRepository userRepository;
    private final LoginTokenRepository loginTokenRepository;
    private final JavaMailSender javaMailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public MagicLinkService(UserRepository userRepository,LoginTokenRepository loginTokenRepository,
                        JavaMailSender javaMailSender)
    {
        this.userRepository = userRepository;
        this.loginTokenRepository = loginTokenRepository;
        this.javaMailSender = javaMailSender;  
    }
    
    @Transactional
    public void sendMagicLink(String email) 
    {
       logger.debug("Entered sendMagicLink()");
       logger.debug("Email received: {}", email);

        //  VALIDATE EMAIL
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
         
       logger.debug("Processing magic link request for {}", email);

        //user exist or not 
        Optional<User> userOpt=userRepository.findByEmail(email.trim());
          
        if(userOpt.isEmpty())
        {
            logger.warn("User not found in DB: {}", email);
            logger.warn("Registration required of email:{}",email);
        
          throw new BusinessException("Please register first as Donor or Volunteer!", 
          "USER_NOT_FOUND");
        }

        User user =userOpt.get();//only for registered one

        logger.debug("User found in DB: {}", email);

        //Generted token
        String token = UUID.randomUUID().toString().replace("-","");
        logger.debug("User found in DB: {}", user.getEmail());

        //for token entity 
        LoginToken loginToken = new LoginToken();
        loginToken.setEmail(user.getEmail());
        loginToken.setToken(token);
        loginToken.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // expires in 10 mins
        loginToken.setUsed(false); // one-time use
        loginToken.setCreatedAt(LocalDateTime.now());
                
        //save token
        loginTokenRepository.save(loginToken);

        //build magic logic 
        //String magicLink = "https://scabbily-avifaunal-pearly.ngrok-free.dev/auth/login?token=" + token;

    String magicLink = baseUrl + "/auth/login?token=" + token;

       logger.debug("Magic link created for {}", email);

       logger.debug("Magic link created {}",magicLink);

        try {

            logger.info("MAIL_USERNAME emailservice= {}", System.getenv("MAIL_USERNAME"));
            logger.info("MAIL_PASSWORD exists emailservice= {}", System.getenv("MAIL_PASSWORD") != null);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("aishwaryapat932@gmail.com");   // Add this line

            message.setTo(email);
            message.setSubject("✨ SayNo2Hunger - Your Magic Login Link");
            
            message.setText("Hii " + user.getName() + "! 👋\n\n" +
            "Your magic link wil ⏰ expire in 10 minutes\n\n" +
                "Together against hunger! ❤️\n" +
                "Team \n"+
                "SayNo2Hunger \n"+
                "Your secure login link:\n" + magicLink + "\n\n");
            
            javaMailSender.send(message);
            logger.info("Attempting to send email to: {}", email);
            logger.info("✅ Email sent to registered user: {}", email);
       } 

       catch (Exception e) 
        {
            logger.error("❌ Email failed: {}", e.getMessage());
            logger.error("Email failed", e);
            logger.warn("Magic link fallback for {}", email);
            logger.warn("Link: {}", magicLink);
            logger.warn("Expires: {}", loginToken.getExpiresAt());
            logger.info("✅ Magic link generated for: {}", email);
        }
    }

    @Transactional
    public User verifyTokenAndLogin(String token) 
    {
        logger.debug("Entered verifyTokenAndLogin()");
        logger.debug("Token received for verification");

        //Validation Check
        if (token == null || token.isBlank()) {
            logger.warn("Login attempt with empty token");
            throw new IllegalArgumentException("Invalid login token");
        }

        //token from database 
        LoginToken loginToken = loginTokenRepository.findByToken(token)
                .orElseThrow(() ->
                {
                        logger.warn("Login attempt with invalid token:{}",token);
                    return new IllegalArgumentException("Login token not found");
                });

        logger.debug("Token fetched from DB");
        logger.debug("Token used: {}", loginToken.getUsed());
        logger.debug("Token expires at: {}", loginToken.getExpiresAt());
        
       //Check if token used 
        if (Boolean.TRUE.equals(loginToken.getUsed())) {
            logger.warn("Attempt to reuse token for email:{}",loginToken.getEmail());
            throw new IllegalStateException("Login token already used");
        }

        //token expired
        if (loginToken.getExpiresAt().isBefore(LocalDateTime.now())) {
        logger.warn("Expired token used for email:{}",loginToken.getEmail());
            throw new IllegalStateException("Login token expired");
        }

        //fetch user using email
        User user = userRepository.findByEmail(loginToken.getEmail())
                .orElseThrow(() ->{
                logger.error("User not found for valid token:{}",loginToken.getEmail());
                    return new IllegalStateException("User not found for this token");
         });

        logger.debug("User fetched: {}", user.getEmail());
        logger.debug("Marking token as used");

        //mark token as token used
        loginToken.setUsed(true);
        loginToken.setVerified(true);
        loginTokenRepository.save(loginToken);

        //return authenticated user
        logger.info("User successfully authenticated:{}",user.getEmail());
        return user;
    }
  
}
