import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleManageRightsComponent } from './role-manage-rights.component';

describe('RoleManageRightsComponent', () => {
  let component: RoleManageRightsComponent;
  let fixture: ComponentFixture<RoleManageRightsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RoleManageRightsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RoleManageRightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
