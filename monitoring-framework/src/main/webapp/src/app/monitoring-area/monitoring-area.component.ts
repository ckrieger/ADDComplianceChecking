import { Component, OnInit } from '@angular/core';
import { MonitoringAreaService } from '../services/monitoring-area.service';
import { AddPatternDialogComponent } from '../add-pattern-dialog/add-pattern-dialog.component';
import { MatDialog, MatSnackBar } from '@angular/material';
import { PatternService } from '../services/pattern.service';
import { PatternInstance } from '../models/PatternInstance';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';
import { MonitoringArea } from '../models/monitoring-area';

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

  refreshMonitoringComponent() {
    this.monitoringAreaService.getAll().subscribe(data => {
      this.monitoringArea = data[0];
      console.log(this.monitoringArea)
    })
    this.rxStompService.watch('/topic/violation').subscribe((message: Message) => {
      let patternInstance: PatternInstance = JSON.parse(message.body);
      console.log(message.body);
      this.openSnackBar(patternInstance.name + ' is violated', 'close');
      this.monitoringAreaService.get(this.monitoringArea.id).subscribe(data => {
        this.monitoringArea = data;
        console.log(data);
      })
    });
  }


  // initializeWebSocketConnection(){
  //   let ws = new SockJS(this.serverUrl);
  //   this.stompClient = Stomp.over(ws);
  //   let that = this;
  //   this.stompClient.connect({}, function(frame) {
  //     that.stompClient.subscribe("/topic/violation", message => {
  //       console.log(message.body);
  //     })
  //   });
  // }

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, {
      duration: 20000,
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

  private updateMonitoringArea() {
    this.monitoringAreaService.update(this.monitoringArea).subscribe(result =>{
      this.refreshMonitoringComponent();
      console.log('updated ' + result);
    });
  }

  private startMonitoring() {
    this.monitoringAreaService.start(this.monitoringArea).subscribe(result => {
      console.log('started monitoring ' + result);
      }
    )
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

  private resetViolationsOfMonitoringArea(){
    this.monitoringArea.patternInstances.forEach(patternInstance => {
      patternInstance.isViolated = false;
    })
    this.monitoringAreaService.update(this.monitoringArea).subscribe(data => {
      this.monitoringArea = data;
    })
  }
}
