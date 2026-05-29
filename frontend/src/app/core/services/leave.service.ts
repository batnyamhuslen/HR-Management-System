import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Leave, LeaveBalance, LeaveRequest } from '../models/leave.models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class LeaveService {
  private url = `${environment.apiUrl}/leaves`;

  constructor(private http: HttpClient) {}

  request(employeeId: number, dto: LeaveRequest): Observable<Leave> {
    return this.http.post<Leave>(`${this.url}/request/${employeeId}`, dto);
  }

  approve(id: number): Observable<Leave> {
    return this.http.put<Leave>(`${this.url}/${id}/approve`, {});
  }

  reject(id: number, note?: string): Observable<Leave> {
    const params = note ? `?note=${note}` : '';
    return this.http.put<Leave>(`${this.url}/${id}/reject${params}`, {});
  }

  getByEmployee(employeeId: number): Observable<Leave[]> {
    return this.http.get<Leave[]>(`${this.url}/employee/${employeeId}`);
  }

  getPending(): Observable<Leave[]> {
    return this.http.get<Leave[]>(`${this.url}/pending`);
  }

  getBalance(employeeId: number): Observable<LeaveBalance> {
    return this.http.get<LeaveBalance>(`${this.url}/balance/${employeeId}`);
  }
}
