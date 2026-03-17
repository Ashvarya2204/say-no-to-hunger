package com.saynotohunger.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.saynotohunger.Entity.User;
import com.saynotohunger.util.SecurityUtils;

@Controller
public class DashboardRedirectController 
{
    @GetMapping("/dashboard")
    public String redirectDashboard()
    {
        User user = SecurityUtils.getCurrentUser();

        boolean isDonor = user.hasRole("FOOD_DONOR");
        boolean isVolunteer = user.hasRole("VOLUNTEER");

        // Only Donor
        if (isDonor && !isVolunteer) {
            return "redirect:/donor/dashboard";
        }

        // Only Volunteer
        if (!isDonor && isVolunteer) {
            return "redirect:/volunteer/dashboard";
        }

        // Both Roles → Show Select Page
        if (isDonor && isVolunteer) {
            return "dashboard/select-role";
        }

        return "redirect:/";
    }

   
}
