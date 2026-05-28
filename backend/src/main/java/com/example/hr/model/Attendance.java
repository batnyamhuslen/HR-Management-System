package com.example.hr.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "attendance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "date"}))
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "check_in")
    private LocalTime checkIn;

    @Column(name = "check_out")
    private LocalTime checkOut;

    // Триггэрээр бодогдох тул insert/update хийхгүй, харин үүссэний дараа DB-ээс уншина
    @Column(name = "late_minutes", insertable = false, updatable = false)
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    private Integer lateMinutes;

    // Double-ийг BigDecimal болгож, NUMERIC(5,2) төрлийг зааж өгөв
    @Column(name = "worked_hours", insertable = false, updatable = false, columnDefinition = "NUMERIC(5,2)")
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    private BigDecimal workedHours;

    @Column(name = "overtime_hours", insertable = false, updatable = false, columnDefinition = "NUMERIC(5,2)")
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    private BigDecimal overtimeHours;

    private String note;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Attendance() {}

    // Конструктор доторх Double төрлүүдийг BigDecimal болгож засав
    public Attendance(Integer id, Employee employee, LocalDate date, LocalTime checkIn, LocalTime checkOut, 
                      Integer lateMinutes, BigDecimal workedHours, BigDecimal overtimeHours, String note, LocalDateTime createdAt) {
        this.id = id;
        this.employee = employee;
        this.date = date;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.lateMinutes = lateMinutes;
        this.workedHours = workedHours;
        this.overtimeHours = overtimeHours;
        this.note = note;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public LocalTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalTime checkIn) { this.checkIn = checkIn; }
    
    public LocalTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalTime checkOut) { this.checkOut = checkOut; }
    
    public Integer getLateMinutes() { return lateMinutes; }
    
    // Getter/Setter-үүдийг BigDecimal болгов
    public BigDecimal getWorkedHours() { return workedHours; }
    public BigDecimal getOvertimeHours() { return overtimeHours; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static AttendanceBuilder builder() {
        return new AttendanceBuilder();
    }

    // Builder Класс
    public static class AttendanceBuilder {
        private Integer id; 
        private Employee employee;
        private LocalDate date;
        private LocalTime checkIn;
        private LocalTime checkOut;
        private String note;

        public AttendanceBuilder id(Integer id) { this.id = id; return this; }
        public AttendanceBuilder employee(Employee employee) { this.employee = employee; return this; }
        public AttendanceBuilder date(LocalDate date) { this.date = date; return this; }
        public AttendanceBuilder checkIn(LocalTime checkIn) { this.checkIn = checkIn; return this; }
        public AttendanceBuilder checkOut(LocalTime checkOut) { this.checkOut = checkOut; return this; }
        public AttendanceBuilder note(String note) { this.note = note; return this; }

        public Attendance build() {
            return new Attendance(id, employee, date, checkIn, checkOut, null, null, null, note, null);
        }
    }
}