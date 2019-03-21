export class PatternInstance {
  constructor(
    public id?: number,
    public name?: string,
    public isActive?: boolean,
    public constraintStatement?: string,
    public isViolated?: boolean,
    public variables?: any[]
  ){
    this.isActive = false;
    this.isViolated = false;
  }
}
