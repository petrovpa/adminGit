import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RightParamsDepartamentComponent } from './right-params-departament.component';

describe('RightParamsDepartamentComponent', () => {
  let component: RightParamsDepartamentComponent;
  let fixture: ComponentFixture<RightParamsDepartamentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RightParamsDepartamentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RightParamsDepartamentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
