import { Component, OnInit } from '@angular/core';
import { EventTypeService } from '../services/event-type.service';

@Component({
  selector: 'app-event-types-overview',
  templateUrl: './event-types-overview.component.html',
  styleUrls: ['./event-types-overview.component.css']
})
export class EventTypesOverviewComponent implements OnInit {

  eventTypes: Array<any>;
  displayedColumns: string[] = ['name', 'type'];
  constructor(private eventTypeService: EventTypeService) { }

  ngOnInit() {
    this.fetchAllEventTypes();
  }

  remove(eventType) {
    this.eventTypeService.remove(eventType.id).subscribe(result => {
      this.fetchAllEventTypes();
    }, error => console.error(error));
  }

  private fetchAllEventTypes() {
    this.eventTypeService.getAll().subscribe(data => {
      this.eventTypes = data;
    })
  }

}
