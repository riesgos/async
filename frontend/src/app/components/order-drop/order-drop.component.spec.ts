import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderDropComponent } from './order-drop.component';

describe('OrderDropComponent', () => {
  let component: OrderDropComponent;
  let fixture: ComponentFixture<OrderDropComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrderDropComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrderDropComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
