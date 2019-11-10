import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserShortInfoComponent } from './user-short-info.component';

describe('UserShortInfoComponent', () => {
  let component: UserShortInfoComponent;
  let fixture: ComponentFixture<UserShortInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserShortInfoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserShortInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
