package com.example.hr.service;

import com.example.hr.dto.EmployeeDto;
import com.example.hr.model.Department;
import com.example.hr.model.Employee;
import com.example.hr.model.Position;
import com.example.hr.repository.DepartmentRepository;
import com.example.hr.repository.EmployeeRepository;
import com.example.hr.repository.PositionRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
    }

    public Page<Employee> getAll(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        if (search != null && !search.isBlank()) {
            return employeeRepository.findByFullNameContainingIgnoreCase(search, pageable);
        }
        return employeeRepository.findAll(pageable);
    }

    public Employee getById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ажилтан олдсонгүй: " + id));
    }

    public Employee create(EmployeeDto dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Имэйл хаяг давхардаж байна: " + dto.getEmail());
        }

        Department dept = dto.getDepartmentId() != null
                ? departmentRepository.findById(dto.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Хэлтэс олдсонгүй"))
                : null;

        Position pos = dto.getPositionId() != null
                ? positionRepository.findById(dto.getPositionId())
                        .orElseThrow(() -> new RuntimeException("Албан тушаал олдсонгүй"))
                : null;

        // Employee модель дээр .isActive() биш .active() гараар бичсэн builder байгаа тул зассан
        Employee employee = Employee.builder()
                .employeeId(generateEmployeeId())
                .fullName(dto.getFullName())
                .age(dto.getAge())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .hireDate(dto.getHireDate())
                .department(dept)
                .position(pos)
                .active(true)
                .build();

        return employeeRepository.save(employee);
    }

    public Employee update(Long id, EmployeeDto dto) {
        Employee employee = getById(id);

        if (!employee.getEmail().equals(dto.getEmail())
                && employeeRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Имэйл хаяг давхардаж байна");
        }

        employee.setFullName(dto.getFullName());
        employee.setAge(dto.getAge());
        employee.setPhone(dto.getPhone());
        employee.setEmail(dto.getEmail());
        employee.setAddress(dto.getAddress());
        employee.setHireDate(dto.getHireDate());

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Хэлтэс олдсонгүй"));
            employee.setDepartment(dept);
        }

        if (dto.getPositionId() != null) {
            Position pos = positionRepository.findById(dto.getPositionId())
                    .orElseThrow(() -> new RuntimeException("Албан тушаал олдсонгүй"));
            employee.setPosition(pos);
        }

        return employeeRepository.save(employee);
    }

    public void delete(Long id) {
        Employee employee = getById(id);
        employee.setActive(false);           // Soft delete
        employeeRepository.save(employee);
    }

    private String generateEmployeeId() {
        return employeeRepository.findLastEmployeeId()
                .map(last -> {
                    int num = Integer.parseInt(last.replace("EMP", "")) + 1;
                    return String.format("EMP%03d", num);
                })
                .orElse("EMP001");
    }
}