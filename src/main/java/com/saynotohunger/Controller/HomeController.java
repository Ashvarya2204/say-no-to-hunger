package com.saynotohunger.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//Controller for landing page 
@Controller
public class HomeController 
{
    @GetMapping("/")
    public String landingPage() 
    {
        return "hero-landing";
    }  

    
}
