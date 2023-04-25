import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrecalcFormComponent } from './precalc-form.component';

describe('PrecalcFormComponent', () => {
  let component: PrecalcFormComponent;
  let fixture: ComponentFixture<PrecalcFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrecalcFormComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrecalcFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
