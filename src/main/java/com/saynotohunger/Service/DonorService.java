package com.saynotohunger.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.saynotohunger.Entity.Donation;
import com.saynotohunger.Entity.DonationStatus;
import com.saynotohunger.Entity.Role;
import com.saynotohunger.Entity.User;
import com.saynotohunger.Exception.BusinessException;
import com.saynotohunger.dao.DonationRepository;
import com.saynotohunger.dao.RoleRepository;
import com.saynotohunger.dao.UserRepository;
import com.saynotohunger.dto.DonorDTO;

@Service
public class DonorService 
{
    private static final Logger logger = LoggerFactory.getLogger(DonorService.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DonationRepository donationRepository;

    public DonorService(UserRepository userRepository,
        RoleRepository roleRepository,DonationRepository donationRepository)
    {
          this.userRepository=userRepository;
          this.roleRepository=roleRepository;
          this.donationRepository=donationRepository;
    }
    
    //create donor
    @Transactional
    public void createDonor(DonorDTO dto)
    {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) 
        {
           throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setIsActive(true);
        user.setProfileCompleted(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        Role donorRole = roleRepository.findByName("FOOD_DONOR").orElseThrow(()->new IllegalStateException("Role not found"));

        user.getRoles().add(donorRole);

        userRepository.save(user);
    }

    //view donor 
    public User getDonerByEmail(String email) 
    {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> 
                new BusinessException("Donor not found", "NOT_FOUND", 404));
    }

    // update Donor
    @Transactional
    public void updateDonor(DonorDTO dto)
    {
        logger.debug("Updating donor in database");

        User donor = userRepository.findByEmail(dto.getEmail())
                .orElseThrow();

        donor.setName(dto.getName());
        donor.setPhone(dto.getPhone());
        donor.setUpdatedAt(LocalDateTime.now());
        userRepository.save(donor);

        logger.info("Donor updated successfully");
    }

    //Delete Donor
    @Transactional
    public void deleteDonor(String email)
    {
        User user=userRepository.findByEmail(email)
        .orElseThrow(()->new RuntimeException("Donor not found"));

        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public List<Donation> getDonorCompletedDonations(User donor)
    {
        return donationRepository
                .findByDonorAndStatusAndCompletionImagePathIsNotNull(
                        donor,
                        DonationStatus.COMPLETED
                );
    }
}

