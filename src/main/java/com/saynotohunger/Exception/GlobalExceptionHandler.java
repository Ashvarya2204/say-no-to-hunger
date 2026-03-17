package com.saynotohunger.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler 
{
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException ex, Model model,HttpServletResponse response) 
    {
        logger.error("Business exception: {} Code: {}", ex.getMessage(), ex.getErrorCode());

        response.setStatus(ex.getHttpStatus());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", ex.getErrorCode());

        return "error";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) 
    {
        logger.error("Runtime exception occurred", ex);

        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "GEN-500");

        return "error";
    }
}
