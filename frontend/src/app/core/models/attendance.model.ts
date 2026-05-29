export interface Attendance {
  id?: number;
  employee?: any;
  date: string;
  checkIn?: string;
  checkOut?: string;
  lateMinutes?: number;
  workedHours?: number;
  overtimeHours?: number;
  note?: string;
}

