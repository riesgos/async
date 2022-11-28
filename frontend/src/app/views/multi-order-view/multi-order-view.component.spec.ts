import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultiOrderViewComponent } from './multi-order-view.component';

describe('MultiOrderViewComponent', () => {
  let component: MultiOrderViewComponent;
  let fixture: ComponentFixture<MultiOrderViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MultiOrderViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MultiOrderViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
