package com.example.hr.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class EmployeeDto {
    @NotBlank
    private String fullName;

    @Min(16) @Max(70)
    private Integer age;

    private String phone;

    @Email @NotBlank
    private String email;

    private String address;

    @NotNull
    private LocalDate hireDate;

    private Long departmentId;
    private Long positionId;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Long getPositionId() { return positionId; }
    public void setPositionId(Long positionId) { this.positionId = positionId; }
}