package com.example.hr.controller;

import com.example.hr.dto.LeaveRequestDto;
import com.example.hr.model.Leave;
import com.example.hr.model.LeaveBalance;
import com.example.hr.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/request/{employeeId}")
    public ResponseEntity<Leave> request(
            @PathVariable Long employeeId,
            @RequestBody @Valid LeaveRequestDto dto) {
        return ResponseEntity.status(201).body(leaveService.requestLeave(employeeId, dto));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<Leave> approve(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.approve(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<Leave> reject(
            @PathVariable Long id,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(leaveService.reject(id, note));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Leave>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getByEmployee(employeeId));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public ResponseEntity<List<Leave>> getPending() {
        return ResponseEntity.ok(leaveService.getPending());
    }

    @GetMapping("/balance/{employeeId}")
    public ResponseEntity<LeaveBalance> getBalance(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getBalance(employeeId));
    }
}