package com.example.hr.model;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_balances",
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "year"}))
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private Integer year;

    @Column(name = "annual_total")
    private Integer annualTotal = 14;

    @Column(name = "annual_used")
    private Integer annualUsed = 0;

    @Column(name = "sick_total")
    private Integer sickTotal = 30;

    @Column(name = "sick_used")
    private Integer sickUsed = 0;

    public LeaveBalance() {}

    public LeaveBalance(Long id, Employee employee, Integer year, Integer annualTotal, Integer annualUsed, Integer sickTotal, Integer sickUsed) {
        this.id = id;
        this.employee = employee;
        this.year = year;
        this.annualTotal = annualTotal != null ? annualTotal : 14;
        this.annualUsed = annualUsed != null ? annualUsed : 0;
        this.sickTotal = sickTotal != null ? sickTotal : 30;
        this.sickUsed = sickUsed != null ? sickUsed : 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Integer getAnnualTotal() { return annualTotal; }
    public void setAnnualTotal(Integer annualTotal) { this.annualTotal = annualTotal; }
    public Integer getAnnualUsed() { return annualUsed; }
    public void setAnnualUsed(Integer annualUsed) { this.annualUsed = annualUsed; }
    public Integer getSickTotal() { return sickTotal; }
    public void setSickTotal(Integer sickTotal) { this.sickTotal = sickTotal; }
    public Integer getSickUsed() { return sickUsed; }
    public void setSickUsed(Integer sickUsed) { this.sickUsed = sickUsed; }

    public static LeaveBalanceBuilder builder() { return new LeaveBalanceBuilder(); }

    public static class LeaveBalanceBuilder {
        private Long id;
        private Employee employee;
        private Integer year;
        private Integer annualTotal = 14;
        private Integer annualUsed = 0;
        private Integer sickTotal = 30;
        private Integer sickUsed = 0;

        public LeaveBalanceBuilder id(Long id) { this.id = id; return this; }
        public LeaveBalanceBuilder employee(Employee employee) { this.employee = employee; return this; }
        public LeaveBalanceBuilder year(Integer year) { this.year = year; return this; }
        public LeaveBalanceBuilder annualTotal(Integer annualTotal) { this.annualTotal = annualTotal; return this; }
        public LeaveBalanceBuilder annualUsed(Integer annualUsed) { this.annualUsed = annualUsed; return this; }
        public LeaveBalanceBuilder sickTotal(Integer sickTotal) { this.sickTotal = sickTotal; return this; }
        public LeaveBalanceBuilder sickUsed(Integer sickUsed) { this.sickUsed = sickUsed; return this; }

        public LeaveBalance build() {
            return new LeaveBalance(id, employee, year, annualTotal, annualUsed, sickTotal, sickUsed);
        }
    }
}