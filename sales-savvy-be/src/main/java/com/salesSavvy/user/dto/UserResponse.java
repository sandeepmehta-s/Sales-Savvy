package com.salesSavvy.user.dto;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String gender;
    private String dob;
    private String role;

    public UserResponse() {}

    public UserResponse(Long id, String username, String email, String gender, String dob, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}