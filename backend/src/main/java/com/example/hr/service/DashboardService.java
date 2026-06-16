package com.example.hr.service;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.example.hr.dto.DashboardDto;
import com.example.hr.repository.AttendanceRepository;
import com.example.hr.repository.EmployeeRepository;
import com.example.hr.repository.LeaveRepository;

@Service
public class DashboardService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;

    public DashboardService(EmployeeRepository employeeRepository,
                            AttendanceRepository attendanceRepository,
                            LeaveRepository leaveRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRepository = leaveRepository;
    }

    public DashboardDto getStats() {
        long totalEmployees = employeeRepository.countByActive(true);
        long todayCheckIns = attendanceRepository.countByDate(LocalDate.now());
        long onLeaveToday = leaveRepository.countOnLeaveToday(LocalDate.now());
        YearMonth now = YearMonth.now();
        long newEmployeesThisMonth = employeeRepository.countByActiveAndCreatedThisMonth(
                true, now.getYear(), now.getMonthValue());
        return new DashboardDto(totalEmployees, todayCheckIns, onLeaveToday, newEmployeesThisMonth);
    }
}
