import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitoringAreaComponent } from './monitoring-area.component';

describe('MonitoringAreaComponent', () => {
  let component: MonitoringAreaComponent;
  let fixture: ComponentFixture<MonitoringAreaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MonitoringAreaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MonitoringAreaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
