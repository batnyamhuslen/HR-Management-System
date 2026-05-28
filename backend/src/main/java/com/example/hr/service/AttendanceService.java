package com.example.hr.service;

import com.example.hr.model.Attendance;
import com.example.hr.model.Employee;
import com.example.hr.repository.AttendanceRepository;
import com.example.hr.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    public Attendance checkIn(Long employeeId, String note) {
        attendanceRepository.findByEmployeeIdAndDate(employeeId, LocalDate.now())
                .ifPresent(a -> {
                    throw new RuntimeException("Өнөөдөр аль хэдийн check-in хийсэн байна");
                });

        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Ажилтан олдсонгүй"));

        Attendance attendance = Attendance.builder()
                .employee(emp)
                .date(LocalDate.now())
                .checkIn(LocalTime.now())
                .note(note)
                .build();

        return attendanceRepository.save(attendance);
    }

    // Бусад шаардлагатай функцуудыг энд үргэлжлүүлэн бичиж болно...
    public Attendance checkOut(Long employeeId) { return null; }
    public Attendance getTodayAttendance(Long employeeId) { return null; }
    public List<Attendance> getMonthlyReport(Long employeeId, int year, int month) { return null; }
    public List<Attendance> getByDateRange(Long employeeId, LocalDate start, LocalDate end) { return null; }
}