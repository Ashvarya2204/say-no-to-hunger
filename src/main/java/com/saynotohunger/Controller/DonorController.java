package com.saynotohunger.Controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.saynotohunger.dto.DonorDTO;
import com.saynotohunger.util.SecurityUtils;
import com.saynotohunger.Entity.Donation;
import com.saynotohunger.Entity.DonationStatus;
import com.saynotohunger.Entity.User;
import com.saynotohunger.Service.DonationService;
import com.saynotohunger.Service.DonorService;
import com.saynotohunger.dao.UserRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/donor")
public class DonorController
{
    private static final Logger logger = LoggerFactory.getLogger(DonorController.class);

    private final DonorService donorService;
    private final DonationService donationService;
    private final UserRepository userRepository;
   
    public DonorController(DonorService donorService,
                     DonationService donationService,
                    UserRepository userRepository)
    {
        this.donorService=donorService;
        this.donationService=donationService;
        this.userRepository=userRepository;
    }

    // Show donor registration form
    @GetMapping("/create")
    public String showCreateForm(Model model) 
    {
        //Empty DTO taki binding easy ho for thymeleaf    
        model.addAttribute("donor", new DonorDTO());

        return "donor-create";
    }

    /*handle donor registration
      @Valid used for validation annotation in DTO and BindingResult stores validation errors
    */

    @PostMapping("/create")
    public String createDonor(
        @Valid @ModelAttribute("donor") DonorDTO dto,
        BindingResult result,Model m,RedirectAttributes redirectAttributes) 
    {
        //If validation fails return back to html page 
        if(result.hasErrors())
        {
            return "donor-create";
        }
    
        donorService.createDonor(dto);

        redirectAttributes.addFlashAttribute(
            "success",
            "Regisration successfully ❤️"
        );

        return "redirect:/login";
    }
    
    //Show the profile 
    @GetMapping("/profile")
    public String viewDonorProfile(Model m)
    {
        User donor = SecurityUtils.getCurrentUser();
        
        logger.debug("Opening donor profile for {}", donor.getEmail());

        m.addAttribute("donor",donor);
        return "donor-profile";
    }

    //show thee data on upadte page 
    @GetMapping("/edit")
    public String showEditForm(Model m)
    {
        User user = SecurityUtils.getCurrentUser();

       logger.debug("Opening donor edit page");

        DonorDTO dto=new DonorDTO();
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());

        m.addAttribute("donor", dto);

        return "donor-edit";
    }

    //handle the update page data 
    @PostMapping("/update")
    public String updateDonor(@ModelAttribute("donor") DonorDTO dto,
                             RedirectAttributes redirectAttributes)
    {
        logger.debug("Updating donor profile");
        logger.debug("Name: {}", dto.getName());
        logger.debug("Email: {}", dto.getEmail());
        logger.debug("Phone: {}", dto.getPhone());

        donorService.updateDonor(dto);

        // refresh security context so updated name appears immediately
        Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

        logger.debug("Logged username: {}", authentication.getName());

        User principalUser = (User) authentication.getPrincipal();

        User updatedUser = userRepository
            .findByEmail(principalUser.getEmail())
            .orElseThrow(() ->
                new RuntimeException("User not found in DB: " + principalUser.getEmail()));
                
        UsernamePasswordAuthenticationToken newAuth =
                    new UsernamePasswordAuthenticationToken(
                            updatedUser,
                            authentication.getCredentials(),
                            authentication.getAuthorities()
                    );

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        redirectAttributes.addFlashAttribute(
                "success",
                "Profile updated successfully ❤️"
            );

        return "redirect:/donor/profile";
    }
    
    //hanle the delete request 
    @PostMapping("/delete")
    public String deleteDonor(@RequestParam String email,
        RedirectAttributes redirectAttributes )
    {
        donorService.deleteDonor(email);

        redirectAttributes.addFlashAttribute(
            "success",
            "Profile deleted"
        );

        return "";
    }
    
    //DashBoard handler
    @PreAuthorize("hasRole('FOOD_DONOR')")
    @GetMapping("/dashboard")
    public String donorDashboard(Model m) 
    {
       logger.debug("Donor dashboard accessed");

       User donor= SecurityUtils.getCurrentUser();

       logger.debug("Logged donor: {}", donor.getEmail());

        List<Donation> donations =
                donationService.getDonorDonations(donor);

        long pendingCount = donations.stream()
                .filter(d -> d.getStatus() == DonationStatus.PENDING)
                .count();

        long completedCount = donations.stream()
                .filter(d -> d.getStatus() == DonationStatus.COMPLETED)
                .count();
        
        logger.debug("Total donations: {}", donations.size());
        logger.debug("Pending donations: {}", pendingCount);
        logger.debug("Completed donations: {}", completedCount);

        m.addAttribute("donorName", donor.getName());
        m.addAttribute("donations", donations);
        m.addAttribute("pendingCount", pendingCount);
        m.addAttribute("completedCount", completedCount);
        m.addAttribute("donorEmail", donor.getEmail());

        logger.debug("Entered donorDashboard()");
        logger.debug("User: {}", donor.getEmail());
        logger.debug("Total donations: {}", donations.size());
        logger.debug("Pending donations: {}", pendingCount);
        logger.debug("Completed donations: {}", completedCount);
        logger.debug("Returning dashboard view");

        return "dashboard";
    }

    @PostMapping("/donation/{id}/cancel")
    public String cancelDonation(@NonNull @PathVariable Long id,
                 RedirectAttributes redirectAttributes) 
    {
       User donor = SecurityUtils.getCurrentUser();

        logger.debug("Cancel donation endpoint hit");
        logger.debug("Donation ID: {}", id);
        logger.debug("Requested by: {}", donor.getEmail());
        
        donationService.cancelDonation(id,donor.getEmail());
           
        redirectAttributes.addFlashAttribute(
            "success",
            "Donation cancelled successfully"
        );

        return "redirect:/donor/dashboard?cancelSuccess";
   }

    @GetMapping("/my-donations")
    public String myDonations(Model model)
    {
        logger.debug("My Donations page opened");

        User donor = SecurityUtils.getCurrentUser();

        logger.debug("Logged donor: {}", donor.getEmail());
        
        List<Donation> donations =
                    donationService.getDonorDonations(donor);
        
        logger.debug("Total donations found: {}", donations.size());

        model.addAttribute("donations", donations);

        return "my-donations";
    }

    @GetMapping("/happiness")
    public String donorHappiness(Model model)
    {
        logger.debug("Opening donor happiness page");

        User donor = SecurityUtils.getCurrentUser();

        List<Donation> donations =
               donorService.getDonorCompletedDonations(donor);

        model.addAttribute("donations", donations);

        return "happiness";
    }

    @GetMapping("/donation/{id}/edit")
    public String showEditDonation(@PathVariable Long id, Model model)
    {
        Donation donation = donationService.getDonationById(id);

        model.addAttribute("donation", donation);

        return "edit-donation";
    }

    @PostMapping("/donation/{id}/update")
    public String updateDonation(
            @PathVariable Long id,
            @ModelAttribute Donation donation,
            RedirectAttributes redirectAttributes) {

        donationService.updateDonation(id, donation);

        redirectAttributes.addFlashAttribute(
            "success",
            "Donation updated successfully ❤️"
        );

        return "redirect:/donor/dashboard";
    }

}
