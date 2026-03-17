package com.saynotohunger.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.saynotohunger.Entity.Donation;
import com.saynotohunger.Entity.FoodCategory;
import com.saynotohunger.Service.DonationService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/donor")
public class DonationController 
{
    private static final Logger logger = LoggerFactory.getLogger(DonationController.class);

    private final DonationService donationService;

    public DonationController(DonationService donationService) 
    {
        this.donationService = donationService;
    }

    @GetMapping("/add")
    public String showForm(Model model) 
    {
       logger.debug("Donation form opened");

        model.addAttribute("donation", new Donation());
        model.addAttribute("foodCategories",FoodCategory.values());
        
        return "donation";
    }

    @PreAuthorize("hasRole('FOOD_DONOR')")
    @PostMapping("/add")
    public String saveDonation(@Valid @ModelAttribute Donation donation,
                               BindingResult result,Model m,
                            RedirectAttributes redirectAttributes) 
    {
        logger.debug("Entered saveDonation()");
        logger.debug("Expiry Time: {}", donation.getExpiryTime());
        logger.debug("Pickup Address: {}", donation.getPickupAddress());
        logger.debug("Quantity: {}", donation.getQuantity());
        logger.debug("Food Name: {}", donation.getFoodName());

        if (result.hasErrors())
        {
            logger.warn("Donation validation failed");
            m.addAttribute("foodCategories", FoodCategory.values());
            return "donation";
        }

        Donation saved=donationService.createDonation(donation);
        
        logger.debug("Donation saved. Redirecting with ID: {}", saved.getId());

         redirectAttributes.addFlashAttribute
        (
            "success",
            "Donation added successfully ❤️"
        );

        return "redirect:/donor/donation/"+saved.getId();
    }

    @GetMapping("/donation/{id}")
    public String viewDonationDetails(@NonNull @PathVariable Long id,Model m)
    {
        logger.debug("Viewing donation details for ID: {}", id);

        Donation donation = donationService.getDonationById(id);

        m.addAttribute("donation", donation);

        return "donation-details";
    }
}
