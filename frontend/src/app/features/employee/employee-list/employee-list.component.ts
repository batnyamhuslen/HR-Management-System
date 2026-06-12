import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EmployeeService } from '../../../core/services/employee.service';
import { Employee } from '../../../core/models/employee.model';
import { EmployeeFormComponent } from '../employee-form/employee-form.component';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    MatTableModule, MatPaginatorModule,
    MatDialogModule, MatSnackBarModule
  ],
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit {
  employees: Employee[] = [];
  totalElements = 0;
  pageSize = 10;
  pageIndex = 0;
  search = '';
  displayedColumns = ['employeeId', 'fullName', 'email', 'department', 'position', 'actions'];


  constructor(
    private employeeService: EmployeeService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.employeeService.getAll(this.pageIndex, this.pageSize, this.search)
      .subscribe(data => {
        this.employees = data.content;
        this.totalElements = data.totalElements;
        this.cdr.detectChanges();
      });
  }

  onSearch(): void {
    this.pageIndex = 0;
    this.load();
  }

  onPage(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.load();
  }

  openForm(employee?: Employee): void {
    const ref = this.dialog.open(EmployeeFormComponent, {
      width: '600px',
      data: employee ?? null
    });
    ref.afterClosed().subscribe(result => {
      if (result) this.load();
    });
  }

  delete(id: number): void {
    if (!confirm('Устгахдаа итгэлтэй байна уу?')) return;
    this.employeeService.delete(id).subscribe({
      next: () => {
        this.snackBar.open('Амжилттай устгалаа', 'Хаах', { duration: 3000 });
        this.load();
      },
      error: () => this.snackBar.open('Алдаа гарлаа', 'Хаах', { duration: 3000 })
    });
  }
}