package com.saynotohunger.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.saynotohunger.Entity.User;
import com.saynotohunger.Exception.BusinessException;

@Component
public class SecurityUtils 
{
    public static User getCurrentUser() 
    {
        System.out.println("We are inside SecurityUtil");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Object principal = auth.getPrincipal();

        if (!(principal instanceof User)) 
        {
            throw new BusinessException("Unauthorized", "ACCESS_DENIED", 403);
        }
        return (User) principal;
    }
}
