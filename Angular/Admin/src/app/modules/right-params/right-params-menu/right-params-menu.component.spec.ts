import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RightParamsMenuComponent } from './right-params-menu.component';

describe('RightParamsMenuComponent', () => {
  let component: RightParamsMenuComponent;
  let fixture: ComponentFixture<RightParamsMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RightParamsMenuComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RightParamsMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
