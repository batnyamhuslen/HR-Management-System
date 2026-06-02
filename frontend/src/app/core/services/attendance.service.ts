import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Attendance } from '../models/attendance.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AttendanceService {
  private url = `${environment.apiUrl}/attendance`;

  constructor(private http: HttpClient) {}

  checkIn(employeeId: number, note?: string): Observable<Attendance> {
    const params = note ? `?note=${note}` : '';
    return this.http.post<Attendance>(`${this.url}/checkin/${employeeId}${params}`, {});
  }

  checkOut(employeeId: number): Observable<Attendance> {
    return this.http.post<Attendance>(`${this.url}/checkout/${employeeId}`, {});
  }

  getToday(employeeId: number): Observable<Attendance> {
    return this.http.get<Attendance>(`${this.url}/today/${employeeId}`);
  }

  getMonthly(employeeId: number, year: number, month: number): Observable<Attendance[]> {
    return this.http.get<Attendance[]>(
      `${this.url}/monthly/${employeeId}?year=${year}&month=${month}`
    );
  }
}

export type { Attendance };
