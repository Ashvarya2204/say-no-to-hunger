package com.saynotohunger.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.saynotohunger.Entity.Donation;
import com.saynotohunger.Entity.DonationStatus;
import com.saynotohunger.Entity.User;

public interface DonationRepository extends JpaRepository<Donation, Long> 
{
    List<Donation> findByDonor(User donor);

    List<Donation> findByDonor_Id(Long donorId);

    @Query("""
        SELECT d 
        FROM Donation d
        LEFT JOIN FETCH d.volunteer
        WHERE d.donor = :donor
    """)
    List<Donation> findByDonorWithVolunteer(@Param("donor") User donor);

    List<Donation> findByStatus(DonationStatus status);

    Optional<Donation> findByIdAndStatus(Long id, DonationStatus status);

    List<Donation> findByStatusAndExpiryTimeBefore(
            DonationStatus status,
            LocalDateTime time);

    List<Donation> findByVolunteerAndStatusNot(
            User volunteer,
            DonationStatus status);

    List<Donation> findByStatusAndCityIgnoreCaseAndExpiryTimeAfter(
            DonationStatus status,
            String city,
            LocalDateTime now);

    List<Donation> findByVolunteerAndStatusIn(
            User volunteer,
            List<DonationStatus> statuses);

    List<Donation> findByVolunteerAndStatus(
            User volunteer,
            DonationStatus status);
    
    List<Donation> findByVolunteer_IdAndStatusIn(
        Long volunteerId,
        List<DonationStatus> statuses);

    List<Donation> findByStatusAndCompletionImagePathIsNotNull(
        DonationStatus status
       );

    List<Donation> findByDonorAndStatusAndCompletionImagePathIsNotNull(
        User donor,
        DonationStatus status
    );

    List<Donation> findByVolunteerAndStatusAndCompletionImagePathIsNotNull(
        User volunteer,
        DonationStatus status
    );

    @Query("SELECT d FROM Donation d LEFT JOIN FETCH d.volunteer WHERE d.id = :id")
    Optional<Donation> findByIdWithVolunteer(@Param("id") Long id);

    List<Donation> findByVolunteer(User volunteer);

    List<Donation> findByDonorAndStatusNot(User donor, DonationStatus status);

    @Query("SELECT d FROM Donation d WHERE d.expiryTime < :now AND (d.status = 'PENDING' OR d.status = 'ACCEPTED')")
    List<Donation> findAllExpired(@Param("now") LocalDateTime now);
    
}