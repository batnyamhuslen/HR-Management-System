import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { EmployeeService } from '../../../core/services/employee.service';
import { Employee } from '../../../core/models/employee.model';

@Component({
  selector: 'app-employee-form',
  standalone: true,
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule,
    MatDialogModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatDatepickerModule, MatNativeDateModule
  ],
  templateUrl: './employee-form.component.html'
})
export class EmployeeFormComponent implements OnInit {
  form: FormGroup;
  isEdit: boolean;

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    public dialogRef: MatDialogRef<EmployeeFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Employee | null
  ) {
    this.isEdit = !!data;
    this.form = this.fb.group({
      fullName: [data?.fullName || '', Validators.required],
      age:      [data?.age || '', [Validators.required, Validators.min(16), Validators.max(70)]],
      phone:    [data?.phone || ''],
      email:    [data?.email || '', [Validators.required, Validators.email]],
      address:  [data?.address || ''],
      hireDate: [data?.hireDate || '', Validators.required]
    });
  }

  ngOnInit(): void {}

  save(): void {
    if (this.form.invalid) return;
    const val = this.form.value;

    const req = this.isEdit
      ? this.employeeService.update(this.data!.id!, val)
      : this.employeeService.create(val);

    req.subscribe({
      next: () => this.dialogRef.close(true),
      error: (err) => alert(err.error?.error || 'Алдаа гарлаа')
    });
  }
}