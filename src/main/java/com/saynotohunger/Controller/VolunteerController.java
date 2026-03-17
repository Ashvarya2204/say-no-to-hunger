package com.saynotohunger.Controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import com.saynotohunger.Entity.Donation;
import com.saynotohunger.Entity.User;
import com.saynotohunger.Entity.VolunteerProfile;
import com.saynotohunger.Service.DonationService;
import com.saynotohunger.Service.VolunteerService;
import com.saynotohunger.dao.UserRepository;
import com.saynotohunger.dto.VolunteerDTO;
import com.saynotohunger.util.SecurityUtils;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
//@PreAuthorize("hasRole('VOLUNTEER')")
@RequestMapping("/volunteer")
public class VolunteerController 
{
    private static final Logger logger = LoggerFactory.getLogger(VolunteerController.class);
    
    private final VolunteerService  volunteerService;
    private final DonationService donationService;
    private final UserRepository userRepository;

    public VolunteerController(VolunteerService volunteerService
        ,DonationService donationService,UserRepository userRepository)
    {
        this.volunteerService=volunteerService;
        this.donationService = donationService;
        this.userRepository=userRepository;
    }

    @GetMapping("/create")
    public String showCreateForm(Model m) 
    { 
        m.addAttribute("volunteer",new VolunteerDTO());
        m.addAttribute("myActiveDonations", List.of()); 
        return "volunteer/create";
    }

    @PostMapping("/create")
    public String createVolunteer(@Valid @ModelAttribute("volunteer") VolunteerDTO dto,
                       BindingResult result,RedirectAttributes redirectAttributes) throws IOException
    {
        if (result.hasErrors()) {
                return "volunteer/create";
            }

        volunteerService.createVolunteer(dto);

        redirectAttributes.addFlashAttribute(
            "success",
            "Registration successfully ❤️"
        );
     
        return "redirect:/login?volunteerRegistered";
    }
    
    @GetMapping("/profile")
    public String profile(Model m)
    {
       logger.debug("Volunteer profile Controller opened");

       User volunteer = SecurityUtils.getCurrentUser();

        VolunteerProfile profile =
                volunteerService.getByEmail(volunteer.getEmail());

        m.addAttribute("profile", profile);

        return "volunteer/profile";
    }
    
    @PostMapping("/toggle")
    public String toggle(@RequestBody String email) 
    {
        volunteerService.toggleAvailability(email);
        return "redirect:/volunteer/profile?email="+email;
    }
    
    @PreAuthorize("hasRole('VOLUNTEER')")
    @PostMapping("/accept/{id}")
    public String acceptDonation(@NonNull @PathVariable Long id,
        RedirectAttributes redirectAttributes) 
    {

        logger.debug("Volunteer accepted donation");

        donationService.acceptDonation(id);

        redirectAttributes.addFlashAttribute(
            "success",
            "Donation accepted successfully ❤️"
        );

        return "redirect:/volunteer/dashboard";
    }

    @PostMapping("/donation/{id}/cancel")
    public String cancelByVolunteer(@NonNull @PathVariable Long id,
        RedirectAttributes redirectAttributes ) 
    {

        User volunteer = SecurityUtils.getCurrentUser();

        logger.debug("Volunteer cancel endpoint hit");
        logger.debug("Donation ID: {}", id);
        logger.debug("Volunteer: {}", volunteer.getEmail());

        donationService.cancelByVolunteer(id, volunteer.getEmail());
        
        redirectAttributes.addFlashAttribute(
            "success",
            "Donation cancelled by volunteer"
        );
        
        return "redirect:/volunteer/dashboard?cancelSuccess";
    }
    
    @PreAuthorize("hasRole('VOLUNTEER')")
    @PostMapping("/pickup/{id}")
    public String pickup(@NonNull @PathVariable Long id,
        RedirectAttributes redirectAttributes) 
    {
        logger.debug("Pickup endpoint hit");

        donationService.markPickedUp(id);

        redirectAttributes.addFlashAttribute(
            "success",
            "picked up successfully ❤️"
        );

        return "redirect:/volunteer/dashboard";
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    @PostMapping("/deliver/{id}")
    public String deliver(@NonNull @PathVariable Long id,
        RedirectAttributes redirectAttributes) 
    {

        logger.debug("Deliver endpoint hit");

        donationService.markDelivered(id);

        redirectAttributes.addFlashAttribute(
            "success",
            "Deliverd successfully ❤️"
        );

        return "redirect:/volunteer/dashboard";
    }

    @PostMapping("/donation/{id}/upload")
    public String uploadCompletionImage(
            @NonNull @PathVariable Long id,
            @RequestParam("image") MultipartFile file,RedirectAttributes redirectAttributes)
            throws IOException 
    {

        donationService.uploadCompletionImage(id, file);
     
        logger.debug("Uploading completion image for donation ID: {}", id);

        redirectAttributes.addFlashAttribute(
            "success",
            "Image Uploaded successfully ❤️"
        );

        return "redirect:/volunteer/dashboard?uploadSuccess";
    }
    
    @PreAuthorize("hasRole('VOLUNTEER')")
    @GetMapping("/dashboard")
    public String volunteerDashboard(Model model) 
    {
            User volunteer = SecurityUtils.getCurrentUser();

            VolunteerProfile profile =
                volunteerService.getByEmail(volunteer.getEmail());

            String city = profile.getCity();
            logger.debug("Volunteer city: {}", city);

            List<Donation> available =
                donationService.getAvailableDonationsByCity(city);

            List<Donation> myActive =
                donationService.getVolunteerActiveDonations(volunteer);

            List<Donation> completed =
                donationService.getVolunteerCompletedDonations(volunteer);

            model.addAttribute("volunteerName", volunteer.getName());
            model.addAttribute("city", city);
            model.addAttribute("availableDonations", available);
            model.addAttribute("myActiveDonations", myActive);
            model.addAttribute("completedDonations", completed);

            return "volunteer/dashboard";
    }

    @GetMapping("/edit")
    public String showEditForm(Model model) 
    {
        logger.debug("Opening volunteer edit form");
        
        User volunteer = SecurityUtils.getCurrentUser();

        VolunteerProfile profile =
                volunteerService.getByEmail(volunteer.getEmail());

        VolunteerDTO dto = new VolunteerDTO();

        dto.setName(profile.getUser().getName());
        dto.setEmail(profile.getUser().getEmail());
        dto.setPhone(profile.getUser().getPhone());
        dto.setGender(profile.getGender());
        dto.setAddress(profile.getAddress());
        dto.setCity(profile.getCity());
        dto.setPinCode(profile.getPincode());

        model.addAttribute("volunteer", dto);

        return "volunteer/edit";
    }

    @PostMapping("/update")
    public String updateVolunteer(
            @ModelAttribute("volunteer") VolunteerDTO dto,RedirectAttributes redirectAttributes) 
    {
        logger.debug("Updating volunteer profile");
        logger.debug("Name: {}", dto.getName());
        logger.debug("Email: {}", dto.getEmail());
        logger.debug("Phone: {}", dto.getPhone());
        logger.debug("Gender: {}", dto.getGender());
        logger.debug("Address: {}", dto.getAddress());
        logger.debug("City: {}", dto.getCity());
        logger.debug("Pincode: {}", dto.getPinCode());
        
        volunteerService.updateVolunteer(dto);

        Authentication authentication =
        SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        User updatedUser = userRepository
        .findById(currentUser.getId())
        .orElseThrow(() -> new RuntimeException("User not found after update"));

       UsernamePasswordAuthenticationToken newAuth =
        new UsernamePasswordAuthenticationToken(
                updatedUser,
                null,
                authentication.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        redirectAttributes.addFlashAttribute(
            "success",
            "Updated successfully ❤️"
        );

        return "redirect:/volunteer/profile?updated";
    }

    @GetMapping("/my-donations")
    public String volunteerDonations(Model model)
    {
       logger.debug("Volunteer My Donations page opened");

        User volunteer = SecurityUtils.getCurrentUser();

        logger.debug("Logged volunteer: {}", volunteer.getEmail());

        List<Donation> donations =
                donationService.getVolunteerDonations(volunteer);

        logger.debug("Total donations found: {}", donations.size());

        model.addAttribute("donations", donations);

        return "volunteer/volunteer-my-donations";
    }

    @GetMapping("/my-happiness")
    public String volunteerHappiness(Model model)
    {
        logger.debug("Opening volunteer happiness page");
        
        User volunteer = SecurityUtils.getCurrentUser();

        List<Donation> donations =
                volunteerService.getVolunteerCompletedDonations(volunteer);

        model.addAttribute("donations", donations);

        return "volunteer/happiness";
    }
}
