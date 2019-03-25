import { Component, OnInit } from '@angular/core';
import { MonitoringAreaService } from '../services/monitoring-area.service';
import { AddPatternDialogComponent } from '../add-pattern-dialog/add-pattern-dialog.component';
import { MatDialog } from '@angular/material';
import { PatternService } from '../services/pattern.service';
import { PatternInstance } from '../models/PatternInstance';

@Component({
  selector: 'app-monitoring-area',
  templateUrl: './monitoring-area.component.html',
  styleUrls: ['./monitoring-area.component.css']
})
export class MonitoringAreaComponent implements OnInit {

  monitoringArea: any;
  patternList: any;
  constructor(
    private patternService: PatternService,
    private monitoringAreaService: MonitoringAreaService,
    public dialog: MatDialog
  ) { }

  ngOnInit() {
    this.patternService.getAll().subscribe(data =>{
      this.patternList = data;
    });
    this.monitoringAreaService.getAll().subscribe(data => {
      this.monitoringArea = data[0];
      console.log(this.monitoringArea)
    })
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
      console.log('updated ' + result);
    });
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
}
