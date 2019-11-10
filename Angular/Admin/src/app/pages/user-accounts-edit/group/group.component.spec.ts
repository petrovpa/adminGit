import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserGroupComponent } from './group.component';

describe('GroupComponent', () => {
  let component: UserGroupComponent;
  let fixture: ComponentFixture<UserGroupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserGroupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
