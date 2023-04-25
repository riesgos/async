import { TestBed } from '@angular/core/testing';

import { PrecalcDataService } from './precalc-data.service';

describe('PrecalcDataService', () => {
  let service: PrecalcDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PrecalcDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
