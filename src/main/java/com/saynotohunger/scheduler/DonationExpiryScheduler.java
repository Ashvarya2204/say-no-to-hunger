package com.saynotohunger.scheduler;

import com.saynotohunger.Entity.Donation;
import com.saynotohunger.Entity.DonationStatus;
import com.saynotohunger.Entity.User;
import com.saynotohunger.Service.EmailService;
import com.saynotohunger.dao.DonationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DonationExpiryScheduler 
{
    private static final Logger logger = LoggerFactory.getLogger(DonationExpiryScheduler.class);

    private final DonationRepository donationRepository;
    private final EmailService emailService;

    public DonationExpiryScheduler(DonationRepository donationRepository,EmailService emailService) 
    {
        this.donationRepository = donationRepository;
        this.emailService = emailService;
    }

        @Scheduled(fixedRate = 600000)
        @Transactional
        public void expireOldDonations() 
        {

                logger.info("Donation expiry scheduler started");

                List<Donation> expiredDonations =
                        donationRepository.findAllExpired(LocalDateTime.now());

                int totalExpired = 0;

                for (Donation donation : expiredDonations) 
                {
                        User volunteer = donation.getVolunteer();

                        donation.setStatus(DonationStatus.EXPIRED);
                        donation.setVolunteer(null);

                        totalExpired++;

                        // Email donor
                        emailService.sendEmail(
                                donation.getDonor().getEmail(),
                                "Donation Expired",
                                "Donation ID " + donation.getId() + " expired automatically."
                        );

                        // Email volunteer (if exists)
                        if (volunteer != null) 
                        {
                                emailService.sendEmail(
                                        volunteer.getEmail(),
                                        "Donation Expired",
                                        "Donation ID " + donation.getId() + " expired."
                                );
                        }
                }

                // ✅ VERY IMPORTANT (you missed this earlier)
                donationRepository.saveAll(expiredDonations);

                logger.info("Total expired donations: {}", totalExpired);
                logger.info("Donation expiry scheduler finished");
        }
}