package com.saynotohunger.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.saynotohunger.Entity.User;
import com.saynotohunger.Entity.VolunteerProfile;

public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile,Long>
{
    Optional<VolunteerProfile> findByUser(User user);
    
    Optional<VolunteerProfile> findByUserEmail(String email);

    @Query("""
       SELECT vp.user 
       FROM VolunteerProfile vp
       WHERE LOWER(vp.city) = LOWER(:city)
       AND vp.active = true
       
       """)

    List<User> findActiveVolunteersByCity(@Param("city") String city);
}
