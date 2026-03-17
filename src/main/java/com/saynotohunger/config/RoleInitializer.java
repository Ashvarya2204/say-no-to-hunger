package com.saynotohunger.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.saynotohunger.Entity.Role;
import com.saynotohunger.dao.RoleRepository;

@Component
public class RoleInitializer implements CommandLineRunner 
{
    private static final Logger logger = LoggerFactory.getLogger(RoleInitializer.class);
    
    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) 
    {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) 
    {

        if (roleRepository.findByName("FOOD_DONOR").isEmpty()) 
        {
            roleRepository.save(new Role("FOOD_DONOR"));
        }

        if (roleRepository.findByName("VOLUNTEER").isEmpty()) 
        {
            roleRepository.save(new Role("VOLUNTEER"));
        }

         logger.info("Default roles checked or created");   
        }
}
