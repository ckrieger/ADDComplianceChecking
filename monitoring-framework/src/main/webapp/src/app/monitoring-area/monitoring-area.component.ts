  import { Component, OnInit } from '@angular/core';
import { MonitoringAreaService } from '../services/monitoring-area.service';
import { AddPatternDialogComponent } from '../add-pattern-dialog/add-pattern-dialog.component';
import { MatDialog, MatSnackBar } from '@angular/material';
import { PatternService } from '../services/pattern.service';
import { PatternInstance } from '../models/PatternInstance';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';
import { MonitoringArea } from '../models/monitoring-area';
import { PatternInstanceViolationMessage } from '../models/pattern-instance-violation-message';

@Component({
  selector: 'app-monitoring-area',
  templateUrl: './monitoring-area.component.html',
  styleUrls: ['./monitoring-area.component.css']
})
export class MonitoringAreaComponent implements OnInit {

  // private serverUrl = 'http:localhost:8080/socket';
  // private stompClient;

  monitoringArea: MonitoringArea;
  patternList: any;

  constructor(
    private patternService: PatternService,
    private monitoringAreaService: MonitoringAreaService,
    public dialog: MatDialog,
    private rxStompService: RxStompService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.patternService.getAll().subscribe(data =>{
      this.patternList = data;
    });
    this.refreshMonitoringComponent();
  }

  orderByName(patternInstances: any) {
    patternInstances.sort(function(a, b){
      if(a.id < b.id) { return -1; }
      if(a.id > b.id) { return 1; }
      return 0;
    });
    return patternInstances;
  }

  refreshMonitoringComponent() {
    this.monitoringAreaService.getAll().subscribe(data => {
      this.monitoringArea = data[0];
      console.log(this.monitoringArea)
    });
    this.rxStompService.watch('/topic/violation').subscribe((message: Message) => {
      let violationMessage: PatternInstanceViolationMessage = JSON.parse(message.body);
      if(violationMessage.patternInstance.name == 'CircuitBreaker'){
        this.openSnackBar(violationMessage.patternInstance.name + ' is violated. Caused by service with ' + violationMessage.violation , 'close', 2000);
      } else {
        this.openSnackBar(violationMessage.patternInstance.name + ' is violated', 'close', 2000);
      }
      this.monitoringAreaService.get(this.monitoringArea.id).subscribe(data => {
        this.monitoringArea = data;
        console.log(data);
      })
    });
  }


  openSnackBar(message: string, action: string, duration: number) {
    this.snackBar.open(message, action, {
      duration: duration,
    });
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(AddPatternDialogComponent, {
      width: '250px',
      data: {patternList: this.patternList, selectedPattern: ''}
    });

    dialogRef.afterClosed().subscribe(result => {
      let patternInstance: PatternInstance = this.createPatternInstance(result);
      this.monitoringArea.patternInstances.push(patternInstance);
      this.updateMonitoringArea();
    });
  }

   updateMonitoringArea() {
    this.monitoringAreaService.update(this.monitoringArea).subscribe(result =>{
      this.refreshMonitoringComponent();
      console.log('updated ' + result);
    });
  }

  private startMonitoring() {
    if(this.isFormComplete()) {
      this.monitoringAreaService.start(this.monitoringArea).subscribe(result => {
        this.openSnackBar("started monitor", "close", 2000);
        this.monitoringArea = result;
      }, error => {
        this.openSnackBar(error.error.message, 'close', 2000)
      })
    } else {
      this.openSnackBar('Please fill out all variable fields of the pattern instances and start monitoring again', 'close', 2000)
    }
  }

  private isFormComplete() {
    let formComplete: boolean = true;
    formComplete = (this.monitoringArea.queueHost.trim() == "" || this.monitoringArea.queueName.trim() == "") ? false : formComplete;
    for (let instance of this.monitoringArea.patternInstances) {
      for (let variable of instance.variables) {
        formComplete = (variable.value.trim() == "") ? false : formComplete;
      }
    }
    return formComplete;
  }

  private stopMonitoring() {
    this.monitoringAreaService.stop(this.monitoringArea).subscribe(result => {
      this.openSnackBar("stopped monitor", "close", 2000);
      this.monitoringArea = result;
    }, err =>{
      this.openSnackBar(err.error.message, 'close', 2000);
    })
  }

  private createPatternInstance(pattern: any) {
    let patternInstance: PatternInstance = new PatternInstance();
    patternInstance.name = pattern.name;
    patternInstance.constraintStatement = pattern.pConstraint;
    patternInstance.isViolated = false;
    patternInstance.isActive = false;
    patternInstance.variables = this.getVariablesOfConstraintStatement(patternInstance.constraintStatement);
    return patternInstance;
  }

  private getVariablesOfConstraintStatement(constraintStatement: string){
    let variables = [];
    let regexp = /(?<=\${).+?(?=\})/g
    let results = constraintStatement.match(regexp);
    results.forEach( result => {
        variables.push({key: result, value: ''})
      }
    );
    return variables;
  }

  private deletePatternInstance(patternInstance: PatternInstance){
    let patternInstances = this.monitoringArea.patternInstances;
    patternInstances.splice(patternInstances.indexOf(patternInstance), 1);
    this.monitoringArea.patternInstances = patternInstances;
    this.monitoringAreaService.update(this.monitoringArea).subscribe(result =>{
      console.log('updated ' + result);
    })
  }

   resetViolationsOfMonitoringArea(){
    this.monitoringArea.patternInstances.forEach(patternInstance => {
      patternInstance.isViolated = false;
    })
    this.monitoringAreaService.update(this.monitoringArea).subscribe(data => {
      this.monitoringArea = data;
    })
  }
}
