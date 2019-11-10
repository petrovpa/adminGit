import { TestBed, inject } from '@angular/core/testing';

import { RightParamsService } from './right-params.service';

describe('RightParamsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RightParamsService]
    });
  });

  it('should be created', inject([RightParamsService], (service: RightParamsService) => {
    expect(service).toBeTruthy();
  }));
});
