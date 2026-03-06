import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-login-view',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login-view.html',
  styleUrl: './login-view.css',
})
export class LoginView {
  private authService = inject(AuthService);
  private router = inject(Router);

  // Estado del formulario con Signals
  credentials = { username: '', password: '' };
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  onLogin() {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.router.navigate(['/projects']);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set('Usuario o contraseña incorrectos. Inténtalo de nuevo.');
        console.error('Login error:', err);
      }
    });
  }
}
