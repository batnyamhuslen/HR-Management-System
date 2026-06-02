import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee, PageResponse } from '../models/employee.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private url = `${environment.apiUrl}/employees`;

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, search = ''): Observable<PageResponse<Employee>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<PageResponse<Employee>>(this.url, { params });
  }

  getById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.url}/${id}`);
  }

  create(employee: Employee): Observable<Employee> {
    return this.http.post<Employee>(this.url, employee);
  }

  update(id: number, employee: Employee): Observable<Employee> {
    return this.http.put<Employee>(`${this.url}/${id}`, employee);
  }

  delete(id: number): Observable<string> {
    return this.http.delete<string>(`${this.url}/${id}`);
  }
}

export type { Employee };
