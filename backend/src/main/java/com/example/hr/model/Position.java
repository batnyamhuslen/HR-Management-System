package com.example.hr.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Position() {}

    public Position(Long id, String title, Department department, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.department = department;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static PositionBuilder builder() { return new PositionBuilder(); }

    public static class PositionBuilder {
        private Long id;
        private String title;
        private Department department;

        public PositionBuilder id(Long id) { this.id = id; return this; }
        public PositionBuilder title(String title) { this.title = title; return this; }
        public PositionBuilder department(Department department) { this.department = department; return this; }

        public Position build() { return new Position(id, title, department, null); }
    }
}