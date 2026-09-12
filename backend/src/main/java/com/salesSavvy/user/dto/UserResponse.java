package com.salesSavvy.user.dto;

public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String gender;
    private String dob;
    private String role;

    public UserResponse() {}

    public UserResponse(String id, String username, String email, String gender, String dob, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.role = role;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
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