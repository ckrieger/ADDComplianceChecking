import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { EventTypeService } from '../services/event-type.service';
import { NgForm } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-event-type-edit',
  templateUrl: './event-type-edit.component.html',
  styleUrls: ['./event-type-edit.component.css']
})
export class EventTypeEditComponent implements OnInit {
  sub: Subscription;
  eventType = {};
  displayedColumns: string[] = ['name', 'type', 'delete'];

  primitiveTypes: any[] = [
    {value: 'java.lang.String', viewValue: 'String'},
    {value: 'java.lang.Integer', viewValue: 'Integer'},
    {value: 'java.lang.Boolean', viewValue: 'Boolean'},
    {value: 'java.lang.Long', viewValue: 'Long'},
    {value: 'java.lang.Double', viewValue: 'Double'},
    {value: 'java.lang.Float', viewValue: 'Float'},
    {value: 'java.lang.Byte', viewValue: 'Byte'}
  ];

  propertyToAdd = {
    name: "",
    type: ""
  };

  // @ts-ignore
  @ViewChild('table', { static: true }) table;
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventTypeService: EventTypeService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      const id = params['id'];
      if (id) {
        this.eventTypeService.get(id).subscribe((eventType: any) => {
          if (eventType) {
            this.eventType = eventType;
          } else {
            console.log(`EventType with id '${id}' not found, returning to list`);
          }
        });
      }
    });
  }

  goToList() {
    this.router.navigate(['/event-type-list']);
  }

  openSnackBar(message: string, action: string, duration: number) {
    this.snackBar.open(message, action, {
      duration: duration,
    });
  }

  deleteProperty(property){
    let index = this.eventType.properties.indexOf(property);
    this.eventType.properties.splice(index, 1);
    this.table.renderRows()
  }

  addProperty(){
      this.eventType.properties.push(JSON.parse(JSON.stringify(this.propertyToAdd))); // deep copy
      this.table.renderRows();
      this.propertyToAdd.type = "";
      this.propertyToAdd.name = "";
    }


  save(form: NgForm) {
    if (form.name.trim() != "") {
      this.eventTypeService.add(this.eventType)
        .subscribe(response => this.goToList(), error => console.error(error));
    } else {
      this.openSnackBar('Event Type name must not be empty', 'close', 2000)
    }
  }

  remove(form: NgForm) {
    this.eventTypeService.remove(form).subscribe(result => {
      this.goToList();
    }, error => console.error(error));
  }

  cancel() {
    this.goToList();
  }
}
