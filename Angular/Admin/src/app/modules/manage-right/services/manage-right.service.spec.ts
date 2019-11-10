import { TestBed, inject } from '@angular/core/testing';

import { RoleManageService } from './role-manage.service';

describe('RoleManageService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RoleManageService]
    });
  });

  it('should be created', inject([RoleManageService], (service: RoleManageService) => {
    expect(service).toBeTruthy();
  }));
});
