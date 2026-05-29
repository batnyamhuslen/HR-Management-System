package com.example.hr.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hr.model.Attendance;
import com.example.hr.model.Employee;
import com.example.hr.repository.AttendanceRepository;
import com.example.hr.repository.EmployeeRepository;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    public Attendance checkIn(Long employeeId, String note) {
        attendanceRepository.findByEmployeeIdAndDate(employeeId, LocalDate.now())
                .ifPresent(a -> {
                    throw new RuntimeException("Өнөөдөр аль хэдийн check-in хийсэн байна");
                });

        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Ажилтан олдсонгүй: " + employeeId));

        Attendance attendance = new Attendance();
        attendance.setEmployee(emp);
        attendance.setDate(LocalDate.now());
        attendance.setCheckIn(LocalTime.now());
        attendance.setNote(note);

        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(Long employeeId) {
        Attendance att = attendanceRepository
                .findByEmployeeIdAndDate(employeeId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Check-in хийгдээгүй байна"));

        if (att.getCheckOut() != null) {
            throw new RuntimeException("Өнөөдөр аль хэдийн check-out хийсэн байна");
        }

        att.setCheckOut(LocalTime.now());
        return attendanceRepository.save(att);
    }

    public Attendance getTodayAttendance(Long employeeId) {
        return attendanceRepository
                .findByEmployeeIdAndDate(employeeId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Өнөөдрийн ирцийн мэдээлэл олдсонгүй"));
    }

    public List<Attendance> getMonthlyReport(Long employeeId, int year, int month) {
        return attendanceRepository.findMonthly(employeeId, year, month);
    }

    public List<Attendance> getByDateRange(Long employeeId, LocalDate start, LocalDate end) {
        return attendanceRepository
                .findByEmployeeIdAndDateBetweenOrderByDateAsc(employeeId, start, end);
    }
}