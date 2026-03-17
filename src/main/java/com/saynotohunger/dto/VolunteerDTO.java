package com.saynotohunger.dto;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;

public class VolunteerDTO 
{ 
    @Email(message = "Invalid email format")
    @NotBlank(message="Email is required")
    private String email;

    @NotBlank(message ="Name is required")
    private String name;

    @NotBlank(message="Phone must be there")
    private String phone;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @Pattern(
        regexp="^[0-9]{6}$",
        message="Pincode must be 6 digits"
    )
    private String pinCode;

    private MultipartFile aadhaarPhoto;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public MultipartFile getAadhaarPhoto() {
        return aadhaarPhoto;
    }

    public void setAadhaarPhoto(MultipartFile aadhaarPhoto) {
        this.aadhaarPhoto = aadhaarPhoto;
    }
    
}
