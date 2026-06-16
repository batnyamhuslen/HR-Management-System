package com.example.hr.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.hr.model.User;
import com.example.hr.model.enums.RoleType;
import com.example.hr.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUser("admin", "admin123", RoleType.ADMIN);
        seedUser("manager", "manager123", RoleType.MANAGER);
    }

    private void seedUser(String username, String password, RoleType role) {
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .build();
            userRepository.save(user);
            System.out.println("Seeded user: " + username + " with role: " + role);
        }
    }
}
