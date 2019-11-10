import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleManageEditComponent } from './role-manage-edit.component';

describe('RoleManageEditComponent', () => {
  let component: RoleManageEditComponent;
  let fixture: ComponentFixture<RoleManageEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RoleManageEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RoleManageEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
