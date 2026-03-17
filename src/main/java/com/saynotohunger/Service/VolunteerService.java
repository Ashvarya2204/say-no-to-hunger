package com.saynotohunger.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.saynotohunger.Entity.Donation;
import com.saynotohunger.Entity.DonationStatus;
import com.saynotohunger.Entity.Role;
import com.saynotohunger.Entity.User;
import com.saynotohunger.Entity.VolunteerProfile;
import com.saynotohunger.Exception.BusinessException;
import com.saynotohunger.dao.DonationRepository;
import com.saynotohunger.dao.RoleRepository;
import com.saynotohunger.dao.UserRepository;
import com.saynotohunger.dao.VolunteerProfileRepository;
import com.saynotohunger.dto.VolunteerDTO;

@Service
public class VolunteerService 
{
        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final VolunteerProfileRepository volunteerProfileRepository;
        private final DonationRepository donationRepository;

        public VolunteerService(UserRepository userRepository,RoleRepository roleRepository,
                                VolunteerProfileRepository volunteerProfileRepository,
                            DonationRepository donationRepository) 
        {
            this.userRepository = userRepository;
            this.roleRepository = roleRepository;
            this.volunteerProfileRepository = volunteerProfileRepository;
            this.donationRepository = donationRepository;
        }

        public VolunteerProfile getByEmail(String email)
        {
            return volunteerProfileRepository
            .findByUserEmail(email)
            .orElseThrow(()->new RuntimeException("Volunteer not found"));
        }

        @Transactional
        public void createVolunteer(VolunteerDTO dto) throws IOException 
        {
            User user;

            //Check if user already exists
            Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());

            if (optionalUser.isPresent()) 
            {
                user = optionalUser.get();

                //If already volunteer → block duplicate
                if (user.hasRole("VOLUNTEER")) 
                {
                    throw new BusinessException("You are already registered as Volunteer", "VOL-409");
                }

                //Add VOLUNTEER role
                Role volunteerRole = roleRepository.findByName("VOLUNTEER")
                        .orElseThrow(() -> new BusinessException("Role not found", "ROLE-404"));

                user.getRoles().add(volunteerRole);

                user.setUpdatedAt(LocalDateTime.now());

                userRepository.save(user);
            } 
            else 
            {
                //Create new user if not exists
                user = new User();
                user.setEmail(dto.getEmail());
                user.setName(dto.getName());
                user.setPhone(dto.getPhone());
                user.setIsActive(true);
                user.setCreatedAt(LocalDateTime.now());

                Role volunteerRole = roleRepository.findByName("VOLUNTEER")
                        .orElseThrow(() -> new BusinessException("Role not found", "ROLE-404"));

                user.getRoles().add(volunteerRole);

                userRepository.save(user);
            }

            //Check if VolunteerProfile already exists
            if (volunteerProfileRepository.findByUser(user).isPresent()) {
                throw new BusinessException("Volunteer profile already exists", "VOL-410");
            }

            //Validate Aadhaar Upload FIRST
            MultipartFile file = dto.getAadhaarPhoto();

            String contentType = file.getContentType();

                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new BusinessException("Only image files allowed", "FILE-401");
                }

                //Safe filename check
                String originalName = file.getOriginalFilename();

                if (originalName == null || !originalName.contains(".")) 
                {
                    throw new BusinessException("Invalid file name", "FILE-402");
                }

                //Size check (2MB)
                if (file.getSize() > 2 * 1024 * 1024) 
                {
                    throw new BusinessException("File must be under 2MB", "FILE-403");
                }
            
                String extension = originalName.substring(originalName.lastIndexOf(".") + 1);

                //Create VolunteerProfile
                VolunteerProfile profile = new VolunteerProfile();
                profile.setUser(user);
                profile.setGender(dto.getGender());
                profile.setAddress(dto.getAddress());
                profile.setCity(dto.getCity());
                profile.setPincode(dto.getPinCode());
                profile.setActive(false);
                profile.setCreatedAt(LocalDateTime.now());

                String fileName = "aadhaar_" + user.getId() + "_"
                + System.currentTimeMillis() + "." + extension;

                Path path = Paths.get("uploads/volunteer/aadhaar/" + fileName);

                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());

                profile.setAadhaarImagePath("uploads/volunteer/aadhaar/" + fileName);

                //Save profile LAST
                volunteerProfileRepository.save(profile);
        }

        @Transactional
        public void updateVolunteer(VolunteerDTO dto)
        {
            VolunteerProfile profile = volunteerProfileRepository
                   .findByUserEmail(dto.getEmail())
                   .orElseThrow(()->new RuntimeException("Volunteer not found"));

            profile.setGender(dto.getGender());
            profile.setAddress(dto.getAddress());
            profile.setCity(dto.getCity());
            profile.setPincode(dto.getPinCode());
            profile.setUpdateAt(LocalDateTime.now());

            profile.getUser().setName(dto.getName());
            profile.getUser().setPhone(dto.getPhone());

            volunteerProfileRepository.save(profile);
        } 

        @Transactional
        public void deleteVolunteer(String email)
        {
            VolunteerProfile profile=volunteerProfileRepository
            .findByUserEmail(email)
            .orElseThrow(()->new RuntimeException("Volunteer not found"));

            profile.setActive(false);
            profile.setDeactivatedAt(LocalDateTime.now());
            profile.getUser().setIsActive(false);
        }

        @Transactional
        public void toggleAvailability(String email)
        {
            VolunteerProfile profile = getByEmail(email);

            if(profile.getActive())
            {
                profile.setActive(false);
                profile.setDeactivatedAt(LocalDateTime.now());
            }
            else
            {
                profile.setActive(true);
                profile.setActivatedAt(LocalDateTime.now());
            }
        }
    
        public List<Donation> getVolunteerCompletedDonations(User volunteer)
        {
            return donationRepository
                    .findByVolunteerAndStatusAndCompletionImagePathIsNotNull(
                            volunteer,
                            DonationStatus.COMPLETED
                    );
        }
}
