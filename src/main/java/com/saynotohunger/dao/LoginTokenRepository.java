package com.saynotohunger.dao;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.saynotohunger.Entity.LoginToken;

public interface LoginTokenRepository extends JpaRepository<LoginToken, Long> 
{

    Optional<LoginToken> findTopByEmailOrderByCreatedAtDesc(String email);
    Optional<LoginToken> findByToken(String token);

    void deleteAllByExpiresAtBefore(LocalDateTime time);
}