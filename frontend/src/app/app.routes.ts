import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'auth/login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'employees',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/employee/employee-list/employee-list').then(m => m.EmployeeListComponent)
  },
  {
    path: 'attendance',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/attendance/attendance').then(m => m.AttendanceComponent)
  },
  {
    path: 'leaves',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/leave/leave').then(m => m.LeaveComponent)
  },
  { path: '**', redirectTo: '/dashboard' }
];