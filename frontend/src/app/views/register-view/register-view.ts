import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from "@angular/forms"

@Component({
  selector: 'app-register-view',
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './register-view.html',
  styleUrl: './register-view.css',
})
export class RegisterView {
  user = { username: '', password: '' };
  loading = signal(false);
  authService = inject(AuthService);
  router = inject(Router);

  onRegister() {
    this.loading.set(true);
    this.authService.register(this.user).subscribe({
      next: () => this.router.navigate(['/login']),
      error: (err) => {
        console.error(err);
        this.loading.set(false);
      }
    });
  }
}
