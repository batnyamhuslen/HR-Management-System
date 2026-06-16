package com.example.hr.repository;

import com.example.hr.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    List<Attendance> findByEmployeeIdAndDateBetweenOrderByDateAsc(
            Long employeeId, LocalDate start, LocalDate end);

    @Query("""
        SELECT a FROM Attendance a
        WHERE a.employee.id = :empId
          AND FUNCTION('YEAR',  a.date) = :year
          AND FUNCTION('MONTH', a.date) = :month
        ORDER BY a.date ASC
    """)
    List<Attendance> findMonthly(
            @Param("empId")  Long empId,
            @Param("year")   int year,
            @Param("month")  int month);

    @Query("""
        SELECT COALESCE(SUM(a.lateMinutes), 0) FROM Attendance a
        WHERE a.employee.id = :empId
          AND FUNCTION('YEAR',  a.date) = :year
          AND FUNCTION('MONTH', a.date) = :month
    """)
    Integer sumLateMinutes(
            @Param("empId")  Long empId,
            @Param("year")   int year,
            @Param("month")  int month);

    long countByDate(LocalDate date);
}
