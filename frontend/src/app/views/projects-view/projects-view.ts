import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ProjectService, Project } from '../../services/project-service';

@Component({
  selector: 'app-projects-view',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './projects-view.html',
  styleUrl: './projects-view.css',
})
export class ProjectsView {
  private projectService = inject(ProjectService);

  projects = signal<Project[]>([]);
  newProject: Project = { name: '', description: '' };
  isLoading = signal(true);

  ngOnInit() {
    this.loadProjects();
  }

  loadProjects() {
    this.projectService.getProjects().subscribe({
      next: (data) => {
        this.projects.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  createProject() {
    if (!this.newProject.name) return;

    this.projectService.createProject(this.newProject).subscribe({
      next: (project) => {
        this.projects.update(prev => [...prev, project]);
        this.newProject = { name: '', description: '' };
        // Cerrar modal (usando el ID nativo del diálogo)
        (document.getElementById('new_project_modal') as any).close();
      }
    });
  }
}
