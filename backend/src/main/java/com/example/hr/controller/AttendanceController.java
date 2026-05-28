package com.example.hr.controller;

import com.example.hr.model.Attendance;
import com.example.hr.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/checkin/{employeeId}")
    public ResponseEntity<Attendance> checkIn(
            @PathVariable Long employeeId,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(attendanceService.checkIn(employeeId, note));
    }

    @PostMapping("/checkout/{employeeId}")
    public ResponseEntity<Attendance> checkOut(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.checkOut(employeeId));
    }

    @GetMapping("/today/{employeeId}")
    public ResponseEntity<Attendance> getToday(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getTodayAttendance(employeeId));
    }

    @GetMapping("/monthly/{employeeId}")
    public ResponseEntity<List<Attendance>> getMonthly(
            @PathVariable Long employeeId,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(attendanceService.getMonthlyReport(employeeId, year, month));
    }

    @GetMapping("/range/{employeeId}")
    public ResponseEntity<List<Attendance>> getByRange(
            @PathVariable Long employeeId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return ResponseEntity.ok(attendanceService.getByDateRange(employeeId, start, end));
    }
}