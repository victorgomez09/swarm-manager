import { Routes } from '@angular/router';
import { authGuard } from './guards/auth-guard'

export const routes: Routes = [
    { path: 'login', loadComponent: () => import('./views/login-view/login-view').then(m => m.LoginView) },
    { path: 'register', loadComponent: () => import('./views/register-view/register-view').then(m => m.RegisterView) },
    {
        path: 'projects',
        canActivate: [authGuard],
        loadComponent: () => import('./views/projects-view/projects-view').then(m => m.ProjectsView)
    },
    { path: '', redirectTo: 'projects', pathMatch: 'full' }
];
