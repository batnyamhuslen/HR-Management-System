package com.example.hr.dto;

import com.example.hr.model.enums.RoleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private RoleType role;

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    private String phone;

    @NotNull
    private LocalDate hireDate;

    private Long departmentId;

    private Long positionId;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Long getPositionId() { return positionId; }
    public void setPositionId(Long positionId) { this.positionId = positionId; }
}
