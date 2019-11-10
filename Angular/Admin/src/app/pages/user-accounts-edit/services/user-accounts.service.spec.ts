import { TestBed, inject } from '@angular/core/testing';

import { UserAccountsService } from './user-accounts.service';

describe('UserAccountsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UserAccountsService]
    });
  });

  it('should be created', inject([UserAccountsService], (service: UserAccountsService) => {
    expect(service).toBeTruthy();
  }));
});
