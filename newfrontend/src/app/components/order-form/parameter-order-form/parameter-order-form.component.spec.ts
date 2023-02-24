import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParameterOrderFormComponent } from './parameter-order-form.component';

describe('ParameterOrderFormComponent', () => {
  let component: ParameterOrderFormComponent;
  let fixture: ComponentFixture<ParameterOrderFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ParameterOrderFormComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParameterOrderFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
