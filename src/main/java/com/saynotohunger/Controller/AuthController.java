package com.saynotohunger.Controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.saynotohunger.Entity.LoginToken;
import com.saynotohunger.Entity.User;
import com.saynotohunger.Exception.BusinessException;
import com.saynotohunger.Service.MagicLinkService;
import com.saynotohunger.dao.LoginTokenRepository;
import com.saynotohunger.util.SecurityUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*This controller handles:User requesting magic link
User clicking magic link*/
@Controller
public class AuthController 
{
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /*Constructor injection for service and Spring will automatically inject MagicLinkService*/
    private final MagicLinkService magicLinkService;
    private final LoginTokenRepository loginTokenRepository;

    public AuthController(MagicLinkService magicLinkService,LoginTokenRepository loginTokenRepository)
    {
        this.magicLinkService=magicLinkService;
        this.loginTokenRepository=loginTokenRepository;
    } 

    /*URL: GET /login opens the login.html
    Shows simple login page where user enters email*/
    @GetMapping("/login")
    public String showLoginPage()
    {
        return "login";
    }

    /*URL: POST /auth/send-link
    Takes email from form and Generates & saves magic link*/
    @PostMapping("/auth/send-link")
    public String sendMagicLink(@RequestParam("email") String email,Model m,
                      RedirectAttributes redirectAttributes) 
    {   
        logger.debug("Entered sendMagicLink()");
        logger.debug("Email received from UI: {}", email);

        try
        {
            //magiclinkservice will be called
            magicLinkService.sendMagicLink(email.trim());

            // Send message to UI
            /*m.addAttribute("message","Magic link send !Check your email");
            m.addAttribute("showTimer", true);
            m.addAttribute("email", email.trim());*/

           m.addAttribute(
                "success",
                "Magic link sent! Check your email 📩"
            );

            return "login";
        }
        catch(BusinessException e)
        {
            logger.error("Error at AuthController in sendMagicLink()", e);

            m.addAttribute("error", e.getMessage());
            m.addAttribute("showRegisterPopup", true);
            m.addAttribute("email",email);

            return "login";
        }
        catch(IllegalArgumentException e)
        {
            logger.error("Error at AuthController in sendMagicLink()", e);

            m.addAttribute("error","Invalid email format");

            return "login";
        }
        catch(Exception e)
        {
            logger.error("Error at AuthController in sendMagicLink()", e);

            m.addAttribute("error","Something want wrong");
            logger.error("Magic link error:",e);

            return "login";
        }
    }
    
    /*URL: GET /auth/login?token=abc
    This is triggered when user clicks the email link*/
    @GetMapping("/auth/login")
    public String verifyMagicLink(@RequestParam("token") String token,Model m,
         HttpSession session,
         HttpServletRequest request,HttpServletResponse response,
         RedirectAttributes redirectAttributes)
    { 
       logger.debug("verifyMagicLink() called");

        // Verify token & fetch authenticated user
       logger.debug("AuthController verifyMagicLink hit");

       logger.debug("Token received: {}", token);

        try
        {
                User user=magicLinkService.verifyTokenAndLogin(token);//fetch token from DB this is the method present in the service class

                logger.debug("User authenticated: {}", user.getEmail());

                logger.debug("User roles: {}", user.getRoles());
                  
                List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_"+role.getName()))
                    .collect(Collectors.toList());

                //this will create spring securyity login session
                Authentication authentication=new UsernamePasswordAuthenticationToken
                    (user,//if here we want only user email than it shold be user.getEmail() but here we are taking whole user from database 
                        null,
                    authorities
                    );

                logger.debug("Authorities created at verify magic link in AuthController: {}", authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.debug("Authentication set in SecurityContext at verify magic link in AuthController");

                logger.debug("Authentication at verify magic link in AuthController: {}", SecurityContextHolder.getContext().getAuthentication());

                redirectAttributes.addFlashAttribute(
                    "success",
                    "Logged in successfully ❤️"
                );

                return "redirect:/dashboard";
        }
        catch(Exception e)
        {
           logger.error("Exception caught at verify magic link in AuthController:", e);
            return "link-error";
        }
    }  

    //token for that email is verified 
    @GetMapping("/auth/status")
    @ResponseBody
    public String checkLoginStatus(@RequestParam String email)
    {
       logger.debug("Checking login status for email: {}", email);

        Optional<LoginToken> tokenOpt=
          loginTokenRepository.findTopByEmailOrderByCreatedAtDesc(email);//fetch latest login token for that email by this method

        if(tokenOpt.isPresent() && Boolean.TRUE.equals(tokenOpt.get().getVerified()))
            return "VERIFIED";

        return "PENDING";
    } 

    @GetMapping("/auth/redirect")
    public String redirectAfterLogin() 
    {
        logger.debug("redirectAfterLogin() called");

        logger.debug("Redirect method executed");

        User user = SecurityUtils.getCurrentUser();

        if(user==null)
            return "redirect:/login";

        return "redirect:/dashboard";
    }

    @GetMapping("/logout-success")
    public String logoutPage()
    {
        return "logout-success";
    }
}
