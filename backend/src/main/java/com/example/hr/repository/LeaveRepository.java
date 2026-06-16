package com.example.hr.repository;

import com.example.hr.model.Leave;
import com.example.hr.model.enums.LeaveStatus;
import com.example.hr.model.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByEmployeeIdOrderByRequestedAtDesc(Long employeeId);

    List<Leave> findByStatus(LeaveStatus status);

    List<Leave> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    List<Leave> findByEmployeeIdAndLeaveType(Long employeeId, LeaveType leaveType);

    @Query("""
        SELECT l FROM Leave l
        WHERE l.employee.id = :empId
          AND l.status = 'PENDING'
        ORDER BY l.requestedAt DESC
    """)
    List<Leave> findPendingByEmployee(@Param("empId") Long empId);

    @Query("""
        SELECT COALESCE(SUM(l.daysCount), 0) FROM Leave l
        WHERE l.employee.id = :empId
          AND l.leaveType  = :type
          AND l.status     = 'APPROVED'
          AND FUNCTION('YEAR', l.startDate) = :year
    """)
    Integer sumApprovedDays(
            @Param("empId") Long empId,
            @Param("type")  LeaveType type,
            @Param("year")  int year);

    @Query("""
        SELECT COUNT(DISTINCT l.employee.id) FROM Leave l
        WHERE l.status = 'APPROVED'
          AND l.startDate <= :today
          AND l.endDate   >= :today
    """)
    long countOnLeaveToday(@Param("today") LocalDate today);
}
