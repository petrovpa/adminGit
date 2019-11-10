import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalDialogGroupComponent } from './modal-dialog-group.component';

describe('ModalDialogGroupComponent', () => {
  let component: ModalDialogGroupComponent;
  let fixture: ComponentFixture<ModalDialogGroupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ModalDialogGroupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalDialogGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
