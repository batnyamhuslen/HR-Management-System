package com.example.hr.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.hr.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByFullNameContainingIgnoreCaseAndActive(String name, boolean active, Pageable pageable);

    Page<Employee> findByFullNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Employee> findByActive(boolean active, Pageable pageable);

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    long countByActive(boolean active);

    @Query("""
        SELECT COUNT(e) FROM Employee e
        WHERE e.active = :active
          AND FUNCTION('YEAR', e.createdAt) = :year
          AND FUNCTION('MONTH', e.createdAt) = :month
    """)
    long countByActiveAndCreatedThisMonth(
            @Param("active") boolean active,
            @Param("year")  int year,
            @Param("month") int month);

    @Query("SELECT MAX(e.employeeId) FROM Employee e")
    Optional<String> findLastEmployeeId();
}