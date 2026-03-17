package com.saynotohunger.websocket;

public class DonationEvent 
{
    private String type;   // NEW, ACCEPTED, PICKED_UP, DELIVERED, COMPLETED
    private Long donationId;
    private String message;

    public DonationEvent(String type, Long donationId, String message) 
    {
        this.type = type;
        this.donationId = donationId;
        this.message = message;
    }

    public String getType() { return type; }
    public Long getDonationId() { return donationId; }
    public String getMessage() { return message; }
}
