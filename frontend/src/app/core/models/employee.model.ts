export interface Employee {
  id?: number;
  employeeId?: string;
  fullName: string;
  age: number;
  phone: string;
  email: string;
  address: string;
  hireDate: string;
  departmentId?: number;
  positionId?: number;
  department?: { id: number; name: string };
  position?: { id: number; title: string };
  active?: boolean;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}