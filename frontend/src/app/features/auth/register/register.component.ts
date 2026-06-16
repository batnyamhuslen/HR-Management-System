import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  form: FormGroup;
  error = '';
  success = '';
  loading = false;

  roles = [
    { value: 'MANAGER', label: 'Менежер' },
    { value: 'EMPLOYEE', label: 'Ажилтан' },
    { value: 'HR', label: 'Хүний нөөц' }
  ];

  departments = [
    { value: 1, label: 'Технологи' },
    { value: 2, label: 'Хүний нөөц' },
    { value: 3, label: 'Санхүү' },
    { value: 4, label: 'Борлуулалт' }
  ];

  positions = [
    { value: 1, label: 'Ахлах хөгжүүлэгч' },
    { value: 2, label: 'Хөгжүүлэгч' },
    { value: 3, label: 'HR менежер' },
    { value: 4, label: 'HR мэргэжилтэн' },
    { value: 5, label: 'Санхүүч' },
    { value: 6, label: 'Борлуулалтын менежер' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(4)]],
      role: ['MANAGER', Validators.required],
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      hireDate: ['', Validators.required],
      departmentId: ['', Validators.required],
      positionId: ['', Validators.required]
    });
  }

  onRegister(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    this.success = '';

    this.authService.register(this.form.value).subscribe({
      next: () => {
        this.success = 'Амжилттай бүртгэгдлээ! Нэвтрэх хуудас руу шилжиж байна...';
        setTimeout(() => this.router.navigate(['/auth/login']), 1500);
      },
      error: (err) => {
        this.error = err.error || 'Бүртгэл амжилтгүй боллоо';
        this.loading = false;
      }
    });
  }
}
