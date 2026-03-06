import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectsView } from './projects-view';

describe('ProjectsView', () => {
  let component: ProjectsView;
  let fixture: ComponentFixture<ProjectsView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectsView],
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectsView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
