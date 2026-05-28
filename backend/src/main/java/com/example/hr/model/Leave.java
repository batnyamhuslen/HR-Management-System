package com.example.hr.model;

import com.example.hr.model.enums.LeaveStatus;
import com.example.hr.model.enums.LeaveType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leaves")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type", columnDefinition = "leave_type")
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "days_count", insertable = false, updatable = false)
    private Integer daysCount;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "leave_status")
    private LeaveStatus status = LeaveStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    private String note;

    public Leave() {}

    public Leave(Long id, Employee employee, LeaveType leaveType, LocalDate startDate, LocalDate endDate, Integer daysCount, String reason, LeaveStatus status, User approvedBy, LocalDateTime requestedAt, LocalDateTime processedAt, String note) {
        this.id = id;
        this.employee = employee;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.daysCount = daysCount;
        this.reason = reason;
        this.status = status != null ? status : LeaveStatus.PENDING;
        this.approvedBy = approvedBy;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.note = note;
    }

    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getDaysCount() { return daysCount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }
    public User getApprovedBy() { return approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public static LeaveBuilder builder() { return new LeaveBuilder(); }

    public static class LeaveBuilder {
        private Long id;
        private Employee employee;
        private LeaveType leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
        private LeaveStatus status = LeaveStatus.PENDING;
        private User approvedBy;
        private String note;

        public LeaveBuilder id(Long id) { this.id = id; return this; }
        public LeaveBuilder employee(Employee employee) { this.employee = employee; return this; }
        public LeaveBuilder leaveType(LeaveType leaveType) { this.leaveType = leaveType; return this; }
        public LeaveBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public LeaveBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public LeaveBuilder reason(String reason) { this.reason = reason; return this; }
        public LeaveBuilder status(LeaveStatus status) { this.status = status; return this; }
        public LeaveBuilder approvedBy(User approvedBy) { this.approvedBy = approvedBy; return this; }
        public LeaveBuilder note(String note) { this.note = note; return this; }

        public Leave build() {
            return new Leave(id, employee, leaveType, startDate, endDate, null, reason, status, approvedBy, null, null, note);
        }
    }
}