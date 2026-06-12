package com.example.hr.dto;

public class LoginResponse {
    private String token;
    private String username;
    private String role;

    public LoginResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getUsername() { 
        System.out.println("Response username: " + username);
        return username; }
    public String getRole() { return role; }
}