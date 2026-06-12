import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { LeaveService } from '../../core/services/leave.service';
import { Leave, LeaveBalance } from '../../core/models/leave.models';

@Component({
  selector: 'app-leave',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule,
    MatTableModule, MatSnackBarModule
  ],
  templateUrl: './leave.component.html',
  styleUrls: ['./leave.component.scss']
})
export class LeaveComponent implements OnInit {
  leaves: Leave[] = [];
  balance: LeaveBalance | null = null;
  form: FormGroup;
  employeeId = 1;
  displayedColumns = ['leaveType', 'startDate', 'endDate', 'daysCount', 'status'];

  constructor(
    private leaveService: LeaveService,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.form = this.fb.group({
      leaveType: ['ANNUAL', Validators.required],
      startDate: ['', Validators.required],
      endDate:   ['', Validators.required],
      reason:    ['']
    });
  }

  ngOnInit(): void {
    this.loadLeaves();
    this.loadBalance();
  }

  loadLeaves(): void {
    this.leaveService.getByEmployee(this.employeeId)
      .subscribe(data => this.leaves = data);
  }

  loadBalance(): void {
    this.leaveService.getBalance(this.employeeId)
      .subscribe(data => this.balance = data);
  }

  submit(): void {
    if (this.form.invalid) return;
    this.leaveService.request(this.employeeId, this.form.value).subscribe({
      next: () => {
        this.snackBar.open('Хүсэлт амжилттай илгээгдлээ', 'Хаах', { duration: 3000 });
        this.form.reset({ leaveType: 'ANNUAL' });
        this.loadLeaves();
        this.loadBalance();
      },
      error: (err) => this.snackBar.open(err.error?.error || 'Алдаа гарлаа', 'Хаах', { duration: 3000 })
    });
  }
}
