package com.saynotohunger.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name="donations")
public class Donation 
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="donor_id",nullable = false)
	private User donor;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="volunteer_id")
	private User volunteer;

	@NotBlank(message="Food name is required")
    private String foodName;

	@NotNull(message = "Food type is required")
	@Enumerated(EnumType.STRING)
	private FoodCategory foodCategory;

	@NotNull(message = "Food type is must")
	@Enumerated(EnumType.STRING)
	private FoodType foodType;

	@NotNull(message ="Quantity is required")
	@Min(value=1,message="Minimum quantity must be 1")
    private Integer quantity;

	@NotBlank(message ="Pickup address is required")
    private String pickupAddress;

	@NotBlank(message ="City is required")
	private String city;
	
	@NotBlank(message = "contact number is required")
    @Column(name = "contact_number")
	private String contactNumber;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private DonationStatus status = DonationStatus.PENDING;

	private LocalDateTime expiryTime;

    private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private LocalDateTime imageUploadedAt;

	private String completionImagePath;

    @PrePersist
	protected void onCreate()
	{
		this.createdAt=LocalDateTime.now();
		this.status=DonationStatus.PENDING;
		System.out.println("Donation created at :"+createdAt);
	}

	@PreUpdate
	protected void onUpdate()
	{
		this.updatedAt=LocalDateTime.now();
		System.out.println("Donation updated at:"+updatedAt);
	}
    
	public LocalDateTime getImageUploadedAt() {
		return imageUploadedAt;
	}

	public void setImageUploadedAt(LocalDateTime imageUploadedAt) {
		this.imageUploadedAt = imageUploadedAt;
	}

	public String getCompletionImagePath() {
		return completionImagePath;
	}

	public void setCompletionImagePath(String completionImagePath) {
		this.completionImagePath = completionImagePath;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getDonor() {
		return donor;
	}

	public void setDonor(User donor) {
		this.donor = donor;
	}

	public User getVolunteer() {
		return volunteer;
	}

	public void setVolunteer(User volunteer) {
		this.volunteer = volunteer;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public FoodCategory getFoodCategory() {
		return foodCategory;
	}

	public void setFoodCategory(FoodCategory foodCategory) {
		this.foodCategory = foodCategory;
	}

	public FoodType getFoodType() {
		return foodType;
	}

	public void setFoodType(FoodType foodType) {
		this.foodType = foodType;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getPickupAddress() {
		return pickupAddress;
	}

	public void setPickupAddress(String pickupAddress) {
		this.pickupAddress = pickupAddress;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public DonationStatus getStatus() {
		return status;
	}

	public void setStatus(DonationStatus status) {
		this.status = status;
	}

	public LocalDateTime getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(LocalDateTime expiryTime) {
		this.expiryTime = expiryTime;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Donation() {
	}

	public Donation(Long id, User donor, User volunteer, @NotBlank(message = "Food name is required") String foodName,
			@NotNull(message = "Food type is required") FoodCategory foodCategory,
			@NotNull(message = "Food type is must") FoodType foodType,
			@NotNull(message = "Quantity is required") @Min(value = 1, message = "Minimum quantity must be 1") Integer quantity,
			@NotBlank(message = "Pickup address is required") String pickupAddress,
			@NotBlank(message = "contact number is required") String contactNumber, DonationStatus status,
			LocalDateTime expiryTime, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime imageUploadedAt,
			String completionImagePath) {
		this.id = id;
		this.donor = donor;
		this.volunteer = volunteer;
		this.foodName = foodName;
		this.foodCategory = foodCategory;
		this.foodType = foodType;
		this.quantity = quantity;
		this.pickupAddress = pickupAddress;
		this.contactNumber = contactNumber;
		this.status = status;
		this.expiryTime = expiryTime;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.imageUploadedAt = imageUploadedAt;
		this.completionImagePath = completionImagePath;
	}

	@Override
	public String toString() {
		return "Donation [id=" + id + ", donor=" + donor + ", volunteer=" + volunteer + ", foodName=" + foodName
				+ ", foodCategory=" + foodCategory + ", foodType=" + foodType + ", quantity=" + quantity
				+ ", pickupAddress=" + pickupAddress + ", contactNumber=" + contactNumber + ", status=" + status
				+ ", expiryTime=" + expiryTime + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
				+ ", imageUploadedAt=" + imageUploadedAt + ", completionImagePath=" + completionImagePath + "]";
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
