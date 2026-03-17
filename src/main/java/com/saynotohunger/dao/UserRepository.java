package com.saynotohunger.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.saynotohunger.Entity.User;

public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

   @Query("""
    SELECT u FROM User u
    JOIN u.roles r
    JOIN VolunteerProfile vp ON vp.user = u
    WHERE r.name = 'VOLUNTEER'
    AND vp.active = true
    AND LOWER(vp.city) LIKE LOWER(CONCAT('%', :city, '%'))
    """)
    List<User> findActiveVolunteersByCityContainingIgnoreCase(@Param("city") String city);
}