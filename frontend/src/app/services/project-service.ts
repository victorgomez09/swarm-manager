import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Project {
  id?: string;
  name: string;
  description: string;
  applications?: any[];
}

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.API_URL}/projects`;

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.API_URL);
  }

  createProject(project: Project): Observable<Project> {
    return this.http.post<Project>(this.API_URL, project);
  }
}