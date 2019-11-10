import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserRightComponent } from './right.component';

describe('UserRightComponent', () => {
  let component: UserRightComponent;
  let fixture: ComponentFixture<UserRightComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserRightComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserRightComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
