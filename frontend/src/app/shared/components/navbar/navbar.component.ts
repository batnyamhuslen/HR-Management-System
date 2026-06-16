import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {

  constructor(public authService: AuthService, private router: Router) {}

  get initials(): string {
    const name = this.authService.getUsername() || '';
    return name.charAt(0).toUpperCase();
  }

  get roleLabel(): string {
    const role = this.authService.getRole();
    switch (role) {
      case 'ADMIN': return 'Админ';
      case 'HR': return 'Хүний нөөц';
      case 'MANAGER': return 'Менежер';
      case 'EMPLOYEE': return 'Ажилтан';
      default: return '';
    }
  }

  closeMenu(): void {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.overlay');
    if (sidebar) sidebar.classList.remove('open');
    if (overlay) overlay.classList.remove('show');
  }

  logout(): void {
    this.authService.logout();
  }
}
