package com.saynotohunger.scheduler;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.saynotohunger.dao.LoginTokenRepository;

@Component
public class TokenCleanupScheduler 
{
    private static final Logger logger =
         LoggerFactory.getLogger(TokenCleanupScheduler.class);

   private final LoginTokenRepository loginTokenRepository;

   public TokenCleanupScheduler(LoginTokenRepository loginTokenRepository)
   {
     this.loginTokenRepository=loginTokenRepository;
   }

   @Scheduled(fixedRate=3600000)
   @Transactional
   public void deleteExpiredTokens()
   {
    loginTokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());

    logger.info("Expired login tokens cleaned up");
   }
}
