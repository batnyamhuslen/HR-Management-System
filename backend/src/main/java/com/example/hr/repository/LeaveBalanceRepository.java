package com.example.hr.repository;

import com.example.hr.model.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    Optional<LeaveBalance> findByEmployeeIdAndYear(Long employeeId, Integer year);

    boolean existsByEmployeeIdAndYear(Long employeeId, Integer year);
}
