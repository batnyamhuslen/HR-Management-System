import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { DashboardService, DashboardStats } from '../../core/services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = { totalEmployees: 0, todayCheckIns: 0, onLeaveToday: 0, attendancePercent: 0, newEmployeesThisMonth: 0 };

  constructor(
    public authService: AuthService,
    private dashboardService: DashboardService
  ) {}

  ngOnInit(): void {
    this.dashboardService.getStats().subscribe(data => this.stats = data);
  }
}
