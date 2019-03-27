import { PatternInstance } from './PatternInstance';

export class MonitoringArea {
  constructor(
    public id?: number,
    public name?: string,
    public patternInstances?: PatternInstance[],
    public  queueName?: string,
    public queueHost?: string,
    public isActive?: string
  ){

  }
}
