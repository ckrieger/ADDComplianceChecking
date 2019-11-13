import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstrumentationTemplateOverviewComponent } from './instrumentation-template-overview.component';

describe('InstrumentationTemplateOverviewComponent', () => {
  let component: InstrumentationTemplateOverviewComponent;
  let fixture: ComponentFixture<InstrumentationTemplateOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstrumentationTemplateOverviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstrumentationTemplateOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
