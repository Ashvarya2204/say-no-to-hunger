package com.saynotohunger.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.saynotohunger.Entity.Donation;
import com.saynotohunger.Entity.DonationStatus;
import com.saynotohunger.Entity.User;
import com.saynotohunger.Exception.BusinessException;
import com.saynotohunger.dao.DonationRepository;
import com.saynotohunger.dao.VolunteerProfileRepository;
import com.saynotohunger.util.SecurityUtils;
import com.saynotohunger.websocket.DonationEvent;

@Service
public class DonationService 
{
        private static final Logger logger = LoggerFactory.getLogger(DonationService.class);

        private final DonationRepository donationRepository;
        private final EmailService emailService;
        private final SimpMessagingTemplate messagingTemplate;
        private final VolunteerProfileRepository volunteerProfileRepository;

        public DonationService(DonationRepository donationRepository,
                EmailService emailService,
            SimpMessagingTemplate messagingTemplate,
          VolunteerProfileRepository volunteerProfileRepository)
        {

                this.donationRepository=donationRepository;
                this.emailService=emailService;
                this.messagingTemplate=messagingTemplate;
                this.volunteerProfileRepository = volunteerProfileRepository;
        }

        //Create Donation
        @Transactional
        public Donation createDonation(Donation donation)
        {
                logger.debug("Entered createDonation()");
                logger.debug("Food: {}", donation.getFoodName());
                logger.debug("Quantity: {}", donation.getQuantity());
                logger.debug("Address: {}", donation.getPickupAddress());
                logger.debug("Expiry: {}", donation.getExpiryTime());

                //the above code will be replace by below line 
                User donor = SecurityUtils.getCurrentUser();

                logger.debug("Logged in donor: {}", donor.getEmail());

                if(donation.getExpiryTime()==null||donation.getExpiryTime().isBefore(LocalDateTime.now()))
                        throw new BusinessException("Expiiry time cannot be in past so it must be at least 1 min in future",
                                        "Invalid expiry",
                                400);

               donation.setDonor(donor);
                
               donation.setCity(donation.getCity().trim().toLowerCase());

               logger.debug("City saved: {}", donation.getCity());

               donation.setStatus(DonationStatus.PENDING);

                donation.setCreatedAt(LocalDateTime.now());
                
                Donation saved = donationRepository.save(donation);
                
               messagingTemplate.convertAndSend(
                "/topic/donations",
                new DonationEvent(
                        "NEW",
                        saved.getId(),
                        "New donation available!"
                )
                );

                logger.debug("City from UI: {}", donation.getCity());

                notifyVolunteers(saved);

                logger.info("Donation saved successfully");
                logger.info("Donation saved in DB");
                logger.debug("Saved ID: {}", saved.getId());
                logger.debug("Saved Status: {}", saved.getStatus());

                return saved;
        }

        //for dectiecting the city
        public List<Donation> getAvailableDonationsByCity(String city) 
        {
                List<Donation> list = donationRepository
                .findByStatusAndCityIgnoreCaseAndExpiryTimeAfter(
                        DonationStatus.PENDING,
                        city,
                        LocalDateTime.now()
                );

               logger.debug("Available donations fetched: {}", list.size());

                for (Donation d : list) {
                logger.debug("Donation ID: {}", d.getId());
                logger.debug("Food: {}", d.getFoodName());
                logger.debug("City: {}", d.getPickupAddress());
                logger.debug("Status: {}", d.getStatus());
                }

                return list;
        }

        //Donor view donation
        public List<Donation> getDonorDonations(User donor)
        {
                logger.debug("Entered getDonorDonations()");
                logger.debug("Fetching donations for donor: {}", donor.getEmail());

                List<Donation> list =
                        donationRepository.findByDonorAndStatusNot(donor, DonationStatus.EXPIRED);
                
                logger.debug("Records fetched from DB: {}", list.size());

                for (Donation d : list) {
                        logger.debug("Donation ID: {}", d.getId());
                        logger.debug("Food: {}", d.getFoodName());
                        logger.debug("Status: {}", d.getStatus());
                }

                return list;
        }
       
        // Active donations
        public List<Donation> getVolunteerActiveDonations(User volunteer) {
        return donationRepository.findByVolunteerAndStatusIn(
                volunteer,
                List.of(DonationStatus.ACCEPTED, DonationStatus.PICKED_UP)
        );
        }

        // Completed donations
        public List<Donation> getVolunteerCompletedDonations(User volunteer) {
        return donationRepository.findByVolunteerAndStatusIn(
                volunteer,
                List.of(
                        DonationStatus.DELIVERED,
                        DonationStatus.COMPLETED)
        );
        }

        //Cancellation by volunter
        @Transactional
       public void cancelByVolunteer(@NonNull Long id, String volunteerEmail) 
       {
                Donation donation = donationRepository.findById(id)
                        .orElseThrow(() ->
                                new BusinessException("Donation not found",
                                        "NOT_FOUND", 404));

                logger.debug("Volunteer cancelling donation: {}", id);

                // Check volunteer ownership
                if (donation.getVolunteer() == null ||
                !donation.getVolunteer().getEmail().equals(volunteerEmail)) {

                throw new BusinessException("Unauthorized",
                        "ACCESS_DENIED", 403);
                }

                // Cannot cancel after pickup
                if (donation.getStatus() == DonationStatus.PICKED_UP ||
                donation.getStatus() == DonationStatus.DELIVERED ||
                donation.getStatus() == DonationStatus.COMPLETED) {

                throw new BusinessException("Too late to cancel",
                        "INVALID_STATE", 400);
                }

                donation.setStatus(DonationStatus.CANCELLED_BY_VOLUNTEER);
                donation.setVolunteer(null);

                donationRepository.save(donation);

                logger.debug("Volunteer removed and status updated");

                // Notify donor
                emailService.sendEmail(
                        donation.getDonor().getEmail(),
                        "Volunteer Cancelled",
                        "Volunteer cancelled donation ID: " + donation.getId()
                );

               logger.info("Volunteer cancelled donation successfully");

        }

        //Donation cancel by donor
        @Transactional
        public void cancelDonation(@NonNull Long id, String currentUserEmail) 
        {

            logger.debug("Entered cancelDonation service");

            Donation donation = donationRepository.findById(id)
                    .orElseThrow(() ->
                            new BusinessException("Donation not found",
                                    "NOT_FOUND", 404));

            // Ownership check
            if (!donation.getDonor().getEmail()
                    .equals(currentUserEmail)) {

                throw new BusinessException("Unauthorized",
                        "ACCESS_DENIED", 403);
            }

            // Cannot cancel after pickup
            if (donation.getStatus() == DonationStatus.PICKED_UP
                    || donation.getStatus() == DonationStatus.DELIVERED
                    || donation.getStatus() == DonationStatus.COMPLETED) {

                throw new BusinessException(
                        "Cannot cancel after pickup",
                        "INVALID_STATE", 400);
            }
            
            donation.setStatus(DonationStatus.CANCELLED_BY_DONOR);
           
            donationRepository.save(donation);
             // If volunteer already accepted
            if (donation.getVolunteer() != null) 
            {
                String volunteerEmail = donation.getVolunteer().getEmail();

                emailService.sendEmail(
                        volunteerEmail,
                        "Donation Cancelled by Donor",
                        "Donation ID " + donation.getId() + " cancelled."
                );

                donation.setVolunteer(null);
            }

           logger.info("Donation cancelled successfully by donor");
        }
          
        @Transactional
        public void updateDonation(@NonNull Long id, Donation updatedData) 
        {
                logger.debug("Entered updateDonation()");

                Authentication auth =
                        SecurityContextHolder.getContext().getAuthentication();
                User donor = (User) auth.getPrincipal();

                Donation donation = donationRepository.findById(id)
                        .orElseThrow(() ->
                                new BusinessException("Donation not found",
                                        "NOT_FOUND", 404));

                // Ownership check
                if (!donation.getDonor().getEmail().equals(donor.getEmail())) {
                        throw new BusinessException("Unauthorized",
                                "ACCESS_DENIED", 403);
                }

                // 🚨 Prevent edit after ACCEPTED
                if (donation.getStatus() != DonationStatus.PENDING) {
                        throw new BusinessException(
                                "Cannot edit after acceptance",
                                "INVALID_STATE", 400);
                }

                // Update allowed fields
                donation.setFoodName(updatedData.getFoodName());
                donation.setQuantity(updatedData.getQuantity());
                donation.setPickupAddress(updatedData.getPickupAddress());
                donation.setExpiryTime(updatedData.getExpiryTime());

                logger.info("Donation updated successfully");
       }

       //view doantion deatils 
       public Donation getDonationById(@NonNull Long id)
       {
               logger.debug("Fetching donation from DB: {}", id);

                User currentUser = SecurityUtils.getCurrentUser();

                Donation donation = donationRepository.findByIdWithVolunteer(id)
                        .orElseThrow(() ->
                                new BusinessException("Donation not found"));

                if (!donation.getDonor().getId().equals(currentUser.getId())) {
                        throw new BusinessException("Unauthorized", "ACCESS_DENIED", 403);
                }

                return donation;
               
       }

        //acceptDonation
        @Transactional
        public void acceptDonation(@NonNull Long id) 
        {

                User volunteer = SecurityUtils.getCurrentUser();

                Donation donation = donationRepository
                        .findByIdAndStatus(id, DonationStatus.PENDING)
                        .orElseThrow(() ->
                                new BusinessException("Donation not available"));

                if (donation.getExpiryTime().isBefore(LocalDateTime.now())) {
                        throw new BusinessException("Expired donation");
                }

                donation.setVolunteer(volunteer);
                donation.setStatus(DonationStatus.ACCEPTED);
                
                donationRepository.save(donation);

                messagingTemplate.convertAndSend(
                "/topic/donations",
                new DonationEvent(
                        "ACCEPTED",
                        donation.getId(),
                        "Donation accepted"
                )
                );
        }

        //markpickup
       @Transactional
        public void markPickedUp(@NonNull Long id) 
        {
               User volunteer = SecurityUtils.getCurrentUser();

                Donation donation = donationRepository.findById(id)
                        .orElseThrow(() ->
                                new BusinessException("Donation not found"));

                if (!donation.getVolunteer().getEmail()
                        .equals(volunteer.getEmail())) {
                        throw new BusinessException("Unauthorized");
                }

                if (donation.getStatus() != DonationStatus.ACCEPTED) {
                        throw new BusinessException("Invalid state");
                }

                donation.setStatus(DonationStatus.PICKED_UP);
                
                donationRepository.save(donation);
                messagingTemplate.convertAndSend(
                "/topic/donations",
                new DonationEvent(
                        "PICKED_UP",
                        //"NEW",
                        donation.getId(),
                        "New donation available!"
                )
                );
        }

        //delivered
       @Transactional
        public void markDelivered(@NonNull Long id) 
        {

                User volunteer = SecurityUtils.getCurrentUser();

                Donation donation = donationRepository.findById(id)
                        .orElseThrow(() ->
                                new BusinessException("Donation not found"));

                if (!donation.getVolunteer().getEmail()
                        .equals(volunteer.getEmail())) {
                        throw new BusinessException("Unauthorized");
                }

                if (donation.getStatus() != DonationStatus.PICKED_UP) {
                        throw new BusinessException("Invalid state");
                }

                donation.setStatus(DonationStatus.DELIVERED);
        
                donationRepository.save(donation);

                messagingTemplate.convertAndSend(
        "/topic/donations",
                new DonationEvent(
                                //"NEW",
                        "DELIVERED",
                                donation.getId(),
                "Donation Delivered...!"
                        )
                        );
        }


        //notification for saved 
       private void notifyVolunteers(Donation donation) 
       {
                logger.debug("notifyVolunteers() called");

                String city = donation.getCity();

                logger.debug("Donation city: {}", city);

                List<User> volunteers =
                        volunteerProfileRepository.findActiveVolunteersByCity(city);

                logger.debug("Volunteers found: {}", volunteers.size());

                for (User volunteer : volunteers) {

                       logger.debug("Sending email to: {}", volunteer.getEmail());

                        emailService.sendEmail(
                                volunteer.getEmail(),
                                "New Donation Available!",
                                "New food donation available in your city.\n\n"
                                        + "Food: " + donation.getFoodName() + "\n"
                                        + "Quantity: " + donation.getQuantity() + "\n\n"
                                        + "Login to accept."
                        );
                }
        }

        //Service logic 
        @Transactional
        public void uploadCompletionImage(@NonNull Long id, MultipartFile file)
                throws IOException 
        {

                User volunteer = SecurityUtils.getCurrentUser();

                Donation donation = donationRepository.findById(id)
                        .orElseThrow(() -> new BusinessException("Donation not found"));

                if (donation.getVolunteer() == null ||
                        !donation.getVolunteer().getEmail()
                                .equals(volunteer.getEmail())) {
                        throw new BusinessException("Unauthorized");
                }

                if (donation.getStatus() != DonationStatus.DELIVERED) {
                        throw new BusinessException("Upload allowed only after delivery");
                }
               
               String contentType = file.getContentType();

                if (file.isEmpty() || contentType == null || !contentType.startsWith("image/")) 
                {
                        throw new BusinessException("Only image allowed");
                }

                String fileName = "completion_" + id + "_" +
                        System.currentTimeMillis() + ".jpg";

                String relativePath = "uploads/completions/" + fileName;

                Path path = Paths.get(relativePath);

                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());

                donation.setCompletionImagePath(relativePath);
                donation.setImageUploadedAt(LocalDateTime.now());
                donation.setStatus(DonationStatus.COMPLETED);
                donationRepository.save(donation);
        }

        public List<Donation> getVolunteerDonations(User volunteer)
        {
          return donationRepository.findByVolunteer(volunteer);
        }

}
