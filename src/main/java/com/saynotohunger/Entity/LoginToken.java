package com.saynotohunger.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "login_token")

public class LoginToken 
{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

        @Column(nullable = false)
            private String email;

    @Column(unique = true, nullable = false,length = 100)
        private String token;
        
          @Column(nullable = false)
        private LocalDateTime expiresAt;

        @Column(nullable = false)
        private Boolean used;

          @Column(nullable = false)
        private LocalDateTime createdAt;

          @Column(nullable = false)
          private Boolean verified=false;

          public Long getId() {
              return id;
          }

          public void setId(Long id) {
              this.id = id;
          }

          public String getEmail() {
              return email;
          }

          public void setEmail(String email) {
              this.email = email;
          }

          public String getToken() {
              return token;
          }

          public void setToken(String token) {
              this.token = token;
          }

          public LocalDateTime getExpiresAt() {
              return expiresAt;
          }

          public void setExpiresAt(LocalDateTime expiresAt) {
              this.expiresAt = expiresAt;
          }

          public Boolean getUsed() {
              return used;
          }

          public void setUsed(Boolean used) {
              this.used = used;
          }

          public LocalDateTime getCreatedAt() {
              return createdAt;
          }

          public void setCreatedAt(LocalDateTime createdAt) {
              this.createdAt = createdAt;
          }

          public Boolean getVerified() {
              return verified;
          }

          public void setVerified(Boolean verified) {
              this.verified = verified;
          }

          public LoginToken(Long id, String email, String token, LocalDateTime expiresAt, Boolean used,
                LocalDateTime createdAt, Boolean verified) {
            this.id = id;
            this.email = email;
            this.token = token;
            this.expiresAt = expiresAt;
            this.used = used;
            this.createdAt = createdAt;
            this.verified = verified;
          }

          @Override
          public String toString() {
            return "LoginToken [id=" + id + ", email=" + email + ", token=" + token + ", expiresAt=" + expiresAt
                    + ", used=" + used + ", createdAt=" + createdAt + ", verified=" + verified + "]";
          }

          public LoginToken() {
          }

        
}
