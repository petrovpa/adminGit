import { TestBed, inject } from '@angular/core/testing';

import { PasswordSettingsService } from './password-settings.service';

describe('PasswordSettingsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PasswordSettingsService]
    });
  });

  it('should be created', inject([PasswordSettingsService], (service: PasswordSettingsService) => {
    expect(service).toBeTruthy();
  }));
});
