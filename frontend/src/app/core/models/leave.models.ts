export type LeaveType = 'ANNUAL' | 'SICK' | 'UNPAID';
export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface LeaveRequest {
  leaveType: LeaveType;
  startDate: string;
  endDate: string;
  reason?: string;
}

export interface Leave {
  id: number;
  employee?: any;
  leaveType: LeaveType;
  startDate: string;
  endDate: string;
  daysCount?: number;
  reason?: string;
  status: LeaveStatus;
  requestedAt?: string;
}

export interface LeaveBalance {
  annualTotal: number;
  annualUsed: number;
  sickTotal: number;
  sickUsed: number;
}