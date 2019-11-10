import { AbstractControl } from '@angular/forms';

export class PasswordValidation {

  static MatchPassword(AC: AbstractControl): any {
    if (AC) {
      const password = AC.get('newPassword').value; // to get value in input tag
      const confirmPassword = AC.get('confirmPassword').value; // to get value in input tag
      if (password !== confirmPassword) {
        AC.get('confirmPassword').setErrors({ MatchPassword: true });
      } else {
        return null;
      }
    }
  }
}
