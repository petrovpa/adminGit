import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleManageRightProfileComponent } from './role-manage-right-profile.component';

describe('RoleManageRightProfileComponent', () => {
  let component: RoleManageRightProfileComponent;
  let fixture: ComponentFixture<RoleManageRightProfileComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RoleManageRightProfileComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RoleManageRightProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
