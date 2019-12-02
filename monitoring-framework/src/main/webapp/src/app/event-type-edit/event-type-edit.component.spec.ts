import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EventTypeEditComponent } from './event-type-edit.component';

describe('EventTypeEditComponent', () => {
  let component: EventTypeEditComponent;
  let fixture: ComponentFixture<EventTypeEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EventTypeEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EventTypeEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
