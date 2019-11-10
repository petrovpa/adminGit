import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageRightSimpleComponent } from './manage-right-simple.component';

describe('ManageRightSimpleComponent', () => {
  let component: ManageRightSimpleComponent;
  let fixture: ComponentFixture<ManageRightSimpleComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ManageRightSimpleComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageRightSimpleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
