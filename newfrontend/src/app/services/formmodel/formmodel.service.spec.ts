import { TestBed } from '@angular/core/testing';

import { FormmodelService } from './formmodel.service';

describe('FormmodelService', () => {
  let service: FormmodelService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FormmodelService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
