import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrecalcFormRangeComponent } from './precalc-form-range.component';

describe('PrecalcFormRangeComponent', () => {
  let component: PrecalcFormRangeComponent;
  let fixture: ComponentFixture<PrecalcFormRangeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrecalcFormRangeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrecalcFormRangeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
