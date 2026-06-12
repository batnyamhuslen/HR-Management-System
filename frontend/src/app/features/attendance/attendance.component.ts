import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AttendanceService } from '../../core/services/attendance.service';
import { Attendance } from '../../core/models/attendance.model';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatSnackBarModule],
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.scss']
})
export class AttendanceComponent implements OnInit {
  today: Attendance | null = null;
  monthly: Attendance[] = [];
  employeeId = 1; // TODO: AuthService-ээс авах
  displayedColumns = ['date', 'checkIn', 'checkOut', 'lateMinutes', 'workedHours', 'overtimeHours'];

  constructor(
    private attendanceService: AttendanceService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadToday();
    this.loadMonthly();
  }

  loadToday(): void {
    this.attendanceService.getToday(this.employeeId).subscribe({
      next: (data) => this.today = data,
      error: () => this.today = null
    });
  }

  loadMonthly(): void {
    const now = new Date();
    this.attendanceService.getMonthly(this.employeeId, now.getFullYear(), now.getMonth() + 1)
      .subscribe(data => this.monthly = data);
  }

  checkIn(): void {
    this.attendanceService.checkIn(this.employeeId).subscribe({
      next: (data) => {
        this.today = data;
        this.snackBar.open('Check-in амжилттай', 'Хаах', { duration: 3000 });
      },
      error: (err) => this.snackBar.open(err.error?.error || 'Алдаа гарлаа', 'Хаах', { duration: 3000 })
    });
  }

  checkOut(): void {
    this.attendanceService.checkOut(this.employeeId).subscribe({
      next: (data) => {
        this.today = data;
        this.snackBar.open('Check-out амжилттай', 'Хаах', { duration: 3000 });
        this.loadMonthly();
      },
      error: (err) => this.snackBar.open(err.error?.error || 'Алдаа гарлаа', 'Хаах', { duration: 3000 })
    });
  }
}