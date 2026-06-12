package com.example.hr.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hr.dto.LeaveRequestDto;
import com.example.hr.model.Employee;
import com.example.hr.model.Leave;
import com.example.hr.model.LeaveBalance;
import com.example.hr.model.enums.LeaveStatus;
import com.example.hr.model.enums.LeaveType;
import com.example.hr.repository.EmployeeRepository;
import com.example.hr.repository.LeaveBalanceRepository;
import com.example.hr.repository.LeaveRepository;

@Service
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final LeaveBalanceRepository balanceRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveService(LeaveRepository leaveRepository,
                        LeaveBalanceRepository balanceRepository,
                        EmployeeRepository employeeRepository) {
        this.leaveRepository = leaveRepository;
        this.balanceRepository = balanceRepository;
        this.employeeRepository = employeeRepository;
    }

    public Leave requestLeave(Long empId, LeaveRequestDto dto) {
        if (!dto.getEndDate().isAfter(dto.getStartDate().minusDays(1))) {
            throw new RuntimeException("Дуусах огноо эхлэх огнооноос өмнө байж болохгүй");
        }

        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Ажилтан олдсонгүй: " + empId));

        int requestedDays = (int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        int year = dto.getStartDate().getYear();

        if (dto.getLeaveType() != LeaveType.UNPAID) {
            LeaveBalance balance = balanceRepository
                    .findByEmployeeIdAndYear(empId, year)
                    .orElseThrow(() -> new RuntimeException(year + " оны leave balance олдсонгүй"));

            if (dto.getLeaveType() == LeaveType.ANNUAL) {
                int remaining = balance.getAnnualTotal() - balance.getAnnualUsed();
                if (requestedDays > remaining) {
                    throw new RuntimeException(
                            "Ээлжийн амралт хүрэлцэхгүй. Үлдэгдэл: " + remaining + " өдөр");
                }
            } else if (dto.getLeaveType() == LeaveType.SICK) {
                int remaining = balance.getSickTotal() - balance.getSickUsed();
                if (requestedDays > remaining) {
                    throw new RuntimeException(
                            "Өвчний чөлөө хүрэлцэхгүй. Үлдэгдэл: " + remaining + " өдөр");
                }
            }
        }

        Leave leave = Leave.builder()
                .employee(emp)
                .leaveType(dto.getLeaveType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        return leaveRepository.save(leave);
    }

    public Leave approve(Long leaveId) {
        Leave leave = findById(leaveId);
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Зөвхөн PENDING төлөвтэй хүсэлтийг шийдвэрлэнэ");
        }
        leave.setStatus(LeaveStatus.APPROVED);
        return leaveRepository.save(leave);
    }

    public Leave reject(Long leaveId, String note) {
        Leave leave = findById(leaveId);
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Зөвхөн PENDING төлөвтэй хүсэлтийг шийдвэрлэнэ");
        }
        leave.setStatus(LeaveStatus.REJECTED);
        leave.setNote(note);
        return leaveRepository.save(leave);
    }

    public List<Leave> getByEmployee(Long empId) {
        return leaveRepository.findByEmployeeIdOrderByRequestedAtDesc(empId);
    }

    public List<Leave> getPending() {
        return leaveRepository.findByStatus(LeaveStatus.PENDING);
    }

    public LeaveBalance getBalance(Long empId) {
        int year = LocalDate.now().getYear();
        return balanceRepository.findByEmployeeIdAndYear(empId, year)
                .orElseThrow(() -> new RuntimeException("Leave balance олдсонгүй"));
    }

    private Leave findById(Long id) {
        return leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Амралтын хүсэлт олдсонгүй: " + id));
    }
}