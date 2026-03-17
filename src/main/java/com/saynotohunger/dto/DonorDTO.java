package com.saynotohunger.dto;

import jakarta.validation.constraints.*;

public class DonorDTO
{
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Name is required")
    private String name;

    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Enter valid 10 digit mobile number"
    )
    private String phone;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    
    
}
