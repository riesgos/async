import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceOrderFormComponent } from './service-order-form.component';

describe('ServiceOrderFormComponent', () => {
  let component: ServiceOrderFormComponent;
  let fixture: ComponentFixture<ServiceOrderFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ServiceOrderFormComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ServiceOrderFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
