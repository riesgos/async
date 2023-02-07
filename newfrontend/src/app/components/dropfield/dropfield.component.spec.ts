import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DropfieldComponent } from './dropfield.component';

describe('DropfieldComponent', () => {
  let component: DropfieldComponent;
  let fixture: ComponentFixture<DropfieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DropfieldComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DropfieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
