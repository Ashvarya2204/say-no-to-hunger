package com.saynotohunger.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name="volunteer_profile")
public class VolunteerProfile 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name="user_id",nullable =false,unique =true)
    private User user;
    private String gender;
    private String address;
    private String city;
    private String pincode;
    private Boolean active;
    private LocalDateTime activatedAt;
    private LocalDateTime deactivatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private String aadhaarImagePath;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
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
    public String getPincode() {
        return pincode;
    }
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public LocalDateTime getActivatedAt() {
        return activatedAt;
    }
    public void setActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }
    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }
    public void setDeactivatedAt(LocalDateTime deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
    public String getAadhaarImagePath() {
        return aadhaarImagePath;
    }
    public void setAadhaarImagePath(String aadhaarImagePath) {
        this.aadhaarImagePath = aadhaarImagePath;
    }
    public VolunteerProfile(long id, User user, String gender, String address, String city, String pincode,
            Boolean active, LocalDateTime activatedAt, LocalDateTime deactivatedAt, LocalDateTime createdAt,
            LocalDateTime updateAt, String aadhaarImagePath) {
        this.id = id;
        this.user = user;
        this.gender = gender;
        this.address = address;
        this.city = city;
        this.pincode = pincode;
        this.active = active;
        this.activatedAt = activatedAt;
        this.deactivatedAt = deactivatedAt;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.aadhaarImagePath = aadhaarImagePath;
    }
    public VolunteerProfile() {
    }
    @Override
    public String toString() {
        return "VolunteerProfile [id=" + id + ", user=" + user + ", gender=" + gender + ", address=" + address
                + ", city=" + city + ", pincode=" + pincode + ", active=" + active + ", activatedAt=" + activatedAt
                + ", deactivatedAt=" + deactivatedAt + ", createdAt=" + createdAt + ", updateAt=" + updateAt
                + ", aadhaarImagePath=" + aadhaarImagePath + "]";
    }

    
}
