package com.example.hr.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.hr.config.JwtUtil;
import com.example.hr.dto.LoginRequest;
import com.example.hr.dto.LoginResponse;
import com.example.hr.dto.RegisterRequest;
import com.example.hr.model.Department;
import com.example.hr.model.Employee;
import com.example.hr.model.Position;
import com.example.hr.model.User;
import com.example.hr.model.enums.RoleType;
import com.example.hr.repository.DepartmentRepository;
import com.example.hr.repository.EmployeeRepository;
import com.example.hr.repository.PositionRepository;
import com.example.hr.repository.UserRepository;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    // Гараар байгуулагч функц бичиж dependency injection хийнэ
    public AuthService(AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       CustomUserDetailsService userDetailsService,
                       UserRepository userRepository, PasswordEncoder passwordEncoder,
                       EmployeeRepository employeeRepository,
                       DepartmentRepository departmentRepository,
                       PositionRepository positionRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Нэвтрэх нэр эсвэл нууц үг буруу байна");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Хэрэглэгч олдсонгүй"));

        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }

     public String registerUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }

        if (request.getRole() == RoleType.ADMIN) {
            throw new RuntimeException("ADMIN role cannot be assigned via registration");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        Employee employee = Employee.builder()
                .employeeId(generateEmployeeId())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .hireDate(request.getHireDate())
                .user(savedUser)
                .active(true)
                .build();

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Хэлтэс олдсонгүй"));
            employee.setDepartment(dept);
        }

        if (request.getPositionId() != null) {
            Position pos = positionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> new RuntimeException("Албан тушаал олдсонгүй"));
            employee.setPosition(pos);
        }

        employeeRepository.save(employee);
        return "User registered successfully!";
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