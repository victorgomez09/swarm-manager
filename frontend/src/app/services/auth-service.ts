import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { tap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API_URL = `${environment.API_URL}/auth`;
  currentUser = signal<any>(null);

  constructor(private http: HttpClient) {
    const token = localStorage.getItem('token');
    if (token) this.currentUser.set({ token });
  }

  login(credentials: any) {
    return this.http.post(`${this.API_URL}/login`, credentials).pipe(
      tap((res: any) => {
        localStorage.setItem('token', res.token);
        this.currentUser.set(res);
      })
    );
  }

  register(userData: any) {
    return this.http.post(`${this.API_URL}/register`, userData);
  }

  logout() {
    localStorage.removeItem('token');
    this.currentUser.set(null);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
}