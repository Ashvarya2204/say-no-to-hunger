package com.saynotohunger.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.saynotohunger.Entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> 
{
    Optional<Role> findByName(String name);
}