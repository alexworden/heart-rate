package com.heartrate.dto;

import jakarta.validation.constraints.Email;

/**
 * DTO for user profile updates.
 * Following user rules:
 * - No enums, using String values
 * - Application-level validation
 */
public class UserProfileUpdateRequest {
    @Email(message = "Invalid email format")
    private String email;
    
    private String firstName;
    private String displayName;
    private String mobileNumber;
    private String address;
    private Integer yearOfBirth;

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getYearOfBirth() { return yearOfBirth; }
    public void setYearOfBirth(Integer yearOfBirth) { this.yearOfBirth = yearOfBirth; }
}
