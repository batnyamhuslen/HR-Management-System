package com.example.hr.dto;

public class DashboardDto {
    private long totalEmployees;
    private long todayCheckIns;
    private long onLeaveToday;
    private double attendancePercent;
    private long newEmployeesThisMonth;

    public DashboardDto(long totalEmployees, long todayCheckIns, long onLeaveToday, long newEmployeesThisMonth) {
        this.totalEmployees = totalEmployees;
        this.todayCheckIns = todayCheckIns;
        this.onLeaveToday = onLeaveToday;
        this.newEmployeesThisMonth = newEmployeesThisMonth;
        this.attendancePercent = totalEmployees > 0
                ? Math.round((double) todayCheckIns / totalEmployees * 100)
                : 0;
    }

    public long getTotalEmployees() { return totalEmployees; }
    public long getTodayCheckIns() { return todayCheckIns; }
    public long getOnLeaveToday() { return onLeaveToday; }
    public double getAttendancePercent() { return attendancePercent; }
    public long getNewEmployeesThisMonth() { return newEmployeesThisMonth; }
}
